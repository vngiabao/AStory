import os
import base64
from flask import Flask, request, jsonify, send_from_directory
from openai import OpenAI
from dotenv import load_dotenv
import prompts

load_dotenv()

app = Flask(__name__, static_folder='.')
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Single in-memory conversation (one session at a time)
conversation = [
    {"role": "developer", "content": prompts.instructions}
]


@app.route('/')
def index():
    return send_from_directory('.', 'index.html')

def make_tts(text):
    s = client.audio.speech.create(
        model="gpt-4o-mini-tts",
        voice="cedar",
        input=text,
        instructions=prompts.speech_instructions,
        response_format="mp3",
    )
    return base64.b64encode(s.read()).decode('utf-8')

# ── Token limit reached ──────────────────────────────────────────────────
def closing_sequence():
    closing_response = client.chat.completions.create(
        model="gpt-5.4-mini",
        messages=conversation + [{"role": "user", "content": (
            "[SYSTEM: The conversation has reached its token limit and must now end. "
            "Give a warm, brief closing message in the same language the user has been speaking. "
            "Let them know the session is wrapping up and that a summary of their story will be provided.]"
        )}]
    )
    closing_reply = closing_response.choices[0].message.content
    closing_audio_b64 = make_tts(closing_reply)

    # Build plain transcript for summarizer (skip system messages)
    transcript_lines = []
    for msg in conversation:
        if msg["role"] == "user":
            transcript_lines.append(f"You: {msg['content']}")
        elif msg["role"] == "assistant":
            transcript_lines.append(f"AI: {msg['content']}")
    transcript = "\n".join(transcript_lines)

    summary_response = client.chat.completions.create(
        model="gpt-5.4-mini",
        messages=[
            {"role": "developer", "content": prompts.summarizer_instructions},
            {"role": "user", "content": transcript}
        ]
    )
    summary = summary_response.choices[0].message.content

    return jsonify({
        "closing_reply": closing_reply,
        "closing_audio": closing_audio_b64,
        "summary":       summary,
        "session_ended": True,
    })

@app.route('/api/chat', methods=['POST'])
def chat():
    data = request.get_json()
    message = (data or {}).get('message', '').strip()
    if not message:
        return jsonify({"error": "Empty message."}), 400

    conversation.append({"role": "user", "content": message})

    response = client.chat.completions.create(
        model="gpt-5.4-mini",
        messages=conversation
    )

    if response.usage.total_tokens >= 2000:
        return closing_sequence()

    reply = response.choices[0].message.content
    conversation.append({"role": "assistant", "content": reply})
    audio_b64 = make_tts(reply)

    # Print cost debugging info to console
    print()
    chat_cost = (response.usage.prompt_tokens * 0.00000015) + (response.usage.completion_tokens * 0.0000006)
    audio_cost = len(reply) * 0.000015
    total_cost = chat_cost + audio_cost
    print(f"Chat cost: ${chat_cost:.6f}, Audio cost: ${audio_cost:.6f}, Total cost: ${total_cost:.6f}")
    print(f"Tokens used: {response.usage.total_tokens} (Prompt: {response.usage.prompt_tokens}, Completion: {response.usage.completion_tokens})")
    print()

    return jsonify({"reply": reply, "audio": audio_b64})


@app.route('/api/transcribe', methods=['POST'])
def transcribe():
    audio = request.files.get('audio')
    if not audio:
        return jsonify({"error": "No audio file."}), 400

    result = client.audio.transcriptions.create(
        model="whisper-1",
        file=(audio.filename or 'audio.webm', audio.stream, audio.content_type),
        language="en"
    )
    
    # Print cost debugging info to console
    audio_length_sec = audio.content_length / (16000 * 2)  # Assuming 16kHz mono 16-bit audio
    transcription_cost = audio_length_sec * 0.000006  # Whisper cost per second
    
    print()
    print(f"Transcription cost: ${transcription_cost:.6f} for {audio_length_sec:.2f} seconds of audio") 
    print()

    return jsonify({"transcript": result.text})


if __name__ == '__main__':
    app.run(debug=True)
