import requests
import threading
from concurrent.futures import ThreadPoolExecutor

VIDEO_ID = 1
URL = f"http://localhost:8080/api/posts/{VIDEO_ID}/view"
REQUESTS = 100

def send_view(i):
    try:
        r = requests.post(URL, timeout=5)
        if r.status_code == 200:
            print(f"[{i}] OK")
        else:
            print(f"[{i}] FAIL {r.status_code}")
    except Exception as e:
        print(f"[{i}] ERROR {e}")

def main():
    print(f"Simulating {REQUESTS} concurrent views...")
    with ThreadPoolExecutor(max_workers=REQUESTS) as executor:
        for i in range(REQUESTS):
            executor.submit(send_view, i)

if __name__ == "__main__":
    main()
