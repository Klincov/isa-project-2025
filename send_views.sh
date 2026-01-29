#!/bin/bash

POST_ID=1
REQUESTS=50
URL="http://localhost:8080/api/posts/$POST_ID/view"

echo "Sending $REQUESTS view requests for post $POST_ID"

for i in $(seq 1 $REQUESTS); do
  curl -s -X POST "$URL" > /dev/null
done

echo "Done."
