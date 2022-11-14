client source: https://github.com/SafdarJamal/quiz-app

setup:

- orchestrator needs data of users (smth that will allow human see who got what score, and something that will let quizapp see which user got which link, so that results can be properly mapped)
- quiz for everyone runs on this single machine
- link requires query param: `ukey`, this param should be random (so that users can't guess access to the quiz before its available) and it should uniquely identify each user, because after quiz ends answers are mapped to user by this key

# we need to provide:

- `/usr/src/server/questions/questions.yaml` in format (this should be a file uploaded by teacher and orchestrator should just dl it):

```yaml
test:
  - question: <question content>
    correct: <correct answer>
    incorrect:
      - <incorrect answer1>
      - <incorrect answer2>
      - ...
  - question: ...
```

- `/usr/src/server/params/params.json` that contain time in seconds for quiz duration and mapping between user key and something that will allow teacher to see whose results are these

example:

```json
{
  "timeS": 20,
  "userMapping": {
    "678jhkhjkj": "Name Surname (email@email)"
  }
}
```

---

# after quiz ends we need to extract:

- `/usr/src/server/answers/asnwers.yaml` and upload them in a way that teacher can see them, results are mapped by human readable string supplied in `params.json/userMapping`

See `quiz-app-server/{params, questions, answers}` for examples.

## All these files MUST be present (bound) before service is run, service exposes TCP 8080 port
