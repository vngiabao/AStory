import os
from dotenv import load_dotenv, find_dotenv
from openai import OpenAI
import prompts

_ = load_dotenv(find_dotenv())
client = OpenAI(
    api_key=os.getenv("OPENAI_API_KEY")
)

def main():
    # Add AI instruction
    conversation = [
        {"role": "system", "content": prompts.summarizer_instructions}
    ]

    with open("conversation_transcript.txt", "r") as f:
        user_input = f.read()
    f.close()

    conversation.append({"role": "user", "content": user_input})

    response = client.chat.completions.create(
        model="gpt-5.4-mini",
        messages=conversation
    )
    summarization = response.choices[0].message.content
    
    # Save the summarization
    with open("summarization.txt", "w") as f:
        f.write(summarization)
    

if __name__ == "__main__":
    main()