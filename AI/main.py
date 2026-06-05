# Problem so far
# This is bringing the whole conversation to the model, which is not ideal. We should only be sending the most recent user input.

import json
import os
from dotenv import load_dotenv, find_dotenv
from openai import OpenAI
import prompts

_ = load_dotenv(find_dotenv())
client = OpenAI(
    api_key=os.getenv("OPENAI_API_KEY")
)

def check_story_complete(conversation):
    messages = [
        {"role": "developer", "content": prompts.completion_checker_instructions},
        {
            "role": "user",
            "content": f"Here is the conversation so far:\n\n{conversation}"
        }
    ]

    response = client.chat.completions.create(
        model="gpt-5.4-mini",
        messages=messages
    )

    content = response.choices[0].message.content

    print("=====================DEBUG: Completion Checker Response=====================")
    print(content)
    print("============================================================================")

    try:
        return json.loads(content)
    except json.JSONDecodeError:
        return {
            "story_complete": False,
            "reason": "Could not parse completion checker response.",
            "missing_info": []
        }

def main():
    conversation = [
        {"role": "developer", "content": prompts.instructions}
    ]
    print("Chat started. Type 'exit' to quit.\n")

    while True:
        user_input = input("Bạn: ").strip()
        if user_input.lower() in {"exit", "quit"}:
            print("Tạm biệt!")
            break
        conversation.append({"role": "user", "content": user_input})

        # completion_result = check_story_complete(conversation)
        # if completion_result.get("story_complete"):
        #     print("Câu chuyện đã đủ hoàn chỉnh để tóm tắt. Kết thúc cuộc trò chuyện.")
        #     break

        response = client.chat.completions.create(
            model="gpt-5.4-mini",
            messages=conversation
        )

        assistant_reply = response.choices[0].message.content
        print(f"AI: {assistant_reply}\n")

        print(response.usage.total_tokens)

        conversation.append({"role": "assistant", "content": assistant_reply})

    # Save the conversation to the transcript file
    with open("conversation_transcript.txt", "w", encoding="utf-8") as f:
        f.write("Conversation Transcript\n")
        f.write("=======================\n\n")

        for message in conversation:
            role = message["role"]
            content = message["content"]

            if role == "developer":
                continue
            elif role == "user":
                f.write(f"Bạn: {content}")
            elif role == "assistant":
                f.write(f"AI: {content}\n")

            f.write("\n")

if __name__ == "__main__":
    main()