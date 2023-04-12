#!/bin/bash

command -v curl >/dev/null 2>&1 || {
  echo "curl is not installed. Installing..."
  apt-get update
  apt-get install -y curl
}

command -v jq >/dev/null 2>&1 || {
  echo "jq is not installed. Installing..."
  apt-get update
  apt-get install -y jq
}

INTERFACE=${INTERFACE:-"localhost"}
PORT=${PORT:-"8000"}
baseURL="http://${INTERFACE}:${PORT}/api/v1"
moviesEndpoint='/movies?from_time=2017-01-13T17:09:42.411&to_time=2025-01-13T17:09:42.411'

moviesResponse=$(curl -s "${baseURL}${moviesEndpoint}")

echo "Movies:"
echo "${moviesResponse}"
echo ""

firstScreeningId=$(echo "${moviesResponse}" | jq -r '.[0].screening_times[0].screening_id')

screeningsEndpoint="/screenings/${firstScreeningId}"

screeningResponse=$(curl -s "${baseURL}${screeningsEndpoint}")

echo "Screening:"
echo "${screeningResponse}"
echo ""

seatId1=$(echo "${screeningResponse}" | jq -r '.seats[0].id')
seatId2=$(echo "${screeningResponse}" | jq -r '.seats[1].id')

reservationsEndpoint="/reservations"

postBody=$(cat <<-JSON
{
    "screeningId": "${firstScreeningId}",
    "firstName": "John",
    "lastName": "Doe",
    "selectedSeatsWithTicketsTypes": [
    {
        "seatId": "${seatId1}",
        "ticketType": "adult"
    },
    {
        "seatId": "${seatId2}",
        "ticketType": "student"
    }
    ]
}
JSON
)

reservationResponse=$(curl -s -X POST -H "Content-Type: application/json" -d "${postBody}" "${baseURL}${reservationsEndpoint}")

echo "Make Reservation:"
echo "${reservationResponse}"
echo ""

reservationId=$(echo "${reservationResponse}" | jq -r '.reservationId')

reservationDetailsEndpoint="/reservations/${reservationId}"

reservationDetailsResponse=$(curl -s "${baseURL}${reservationDetailsEndpoint}")

echo "Reservation Details:"
echo "${reservationDetailsResponse}"
echo ""
