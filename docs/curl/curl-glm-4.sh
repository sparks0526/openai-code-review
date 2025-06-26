curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiOWE2YzMyNjIzOTc1NDM5ZmEwNDQzMDdjYzE4YzAzMWQiLCJleHAiOjE3NTA5MDY5OTQyNTksInRpbWVzdGFtcCI6MTc1MDkwNTE5NDI2MH0.XicJn0BKTDdKLhF6ChXF-m8PdVoXGCU9dFgDbYHKBJY" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-4",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "1+1"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions
