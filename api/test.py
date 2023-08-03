# flake8: noqa
import os

import httpx
from dotenv import load_dotenv  # pip install python-dotenv --break-system-packages

# load the .env file

load_dotenv()

SECRET = os.getenv("CRAFT_DAO_ESCROW_SECRET")

# secret, wallet, ucraft_amount

# post request to localhost:4000 with the some json data
response = httpx.post(
    "http://localhost:4000/v1/dao/make_payment",
    json={
        "secret": SECRET,
        "wallet": "juno10r39fueph9fq7a6lgswu4zdsg8t3gxlq670lt0",
        "ucraft_amount": "100",
    },
)
response = httpx.post(
    "http://localhost:4000/v1/dao/make_payment",
    json={
        "secret": SECRET,
        "wallet": "juno10r39fueph9fq7a6lgswu4zdsg8t3gxlq670lt0",
        "ucraft_amount": "13",
    },
)

print(response.json())
