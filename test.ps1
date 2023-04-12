$INTERFACE = $env:INTERFACE
$PORT = $env:PORT
$baseURL = "http://$INTERFACE" + ":$PORT/api/v1"
$moviesEndpoint = '/movies?from_time="2017-01-13T17:09:42.411"&to_time="2025-01-13T17:09:42.411"'

$moviesResponse = Invoke-WebRequest -Uri ($baseURL + $moviesEndpoint) -Method GET

Write-Output "Movies:"
Write-Output $moviesResponse.Content
Write-Output ""
Write-Output ""
Write-Output ""

$moviesJson = $moviesResponse | ConvertFrom-Json

$firstScreeningId = $moviesJson[0].screening_times[0].screening_id

$screeningsEndpoint = "/screenings/$firstScreeningId"

$screeningResponse = Invoke-WebRequest -Uri ($baseURL + $screeningsEndpoint) -Method GET

Write-Output "Screening:"
Write-Output $screeningResponse.Content
Write-Output ""
Write-Output ""
Write-Output ""

$screeningJson = $screeningResponse | ConvertFrom-Json

$seatId1 = $screeningJson.seats[0].id
$seatId2 = $screeningJson.seats[1].id

$reservationsEndpoint = "/reservations"

$postBody = @{
    screeningId = $firstScreeningId
    firstName = "John"
    lastName = "Doe"
    selectedSeatsWithTicketsTypes = @(
    @{
        seatId = $seatId1
        ticketType = "adult"
    },
    @{
        seatId = $seatId2
        ticketType = "student"
    }
    )
} | ConvertTo-Json

$reservationResponse = Invoke-WebRequest -Uri ($baseURL + $reservationsEndpoint) -Method POST -Body $postBody -ContentType "application/json"

Write-Output "Make Reservation:"
Write-Output $reservationResponse.Content
Write-Output ""
Write-Output ""
Write-Output ""

$reservationJson = $reservationResponse | ConvertFrom-Json

$reservationId = $reservationJson.reservationId

$reservationDetailsEndpoint = "/reservations/$reservationId"

$reservationDetailsResponse = Invoke-WebRequest -Uri ($baseURL + $reservationDetailsEndpoint) -Method GET

Write-Output "Reservation Details:"
Write-Output $reservationDetailsResponse.Content
Write-Output ""
Write-Output ""
Write-Output ""
