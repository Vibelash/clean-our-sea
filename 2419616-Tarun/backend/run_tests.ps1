$OutFile = 'curl_test_output.txt'
Set-Content -Path $OutFile -Value ''
Add-Content $OutFile "GET /api/communities"
(Invoke-RestMethod -Uri 'http://localhost:8080/api/communities' -Method Get) | ConvertTo-Json -Depth 10 | Add-Content $OutFile
Add-Content $OutFile ""
Add-Content $OutFile "GET /api/communities/1"
(Invoke-RestMethod -Uri 'http://localhost:8080/api/communities/1' -Method Get) | ConvertTo-Json -Depth 10 | Add-Content $OutFile
Add-Content $OutFile ""
Add-Content $OutFile "POST /api/communities"
$body = @{name='Test Community'; description='Created by test'; category='events'} | ConvertTo-Json
(Invoke-RestMethod -Uri 'http://localhost:8080/api/communities' -Method Post -ContentType 'application/json' -Body $body) | ConvertTo-Json -Depth 10 | Add-Content $OutFile
Add-Content $OutFile ""
Add-Content $OutFile "POST /api/communities/1/join"
(Invoke-RestMethod -Uri 'http://localhost:8080/api/communities/1/join' -Method Post) | ConvertTo-Json -Depth 10 | Add-Content $OutFile
Add-Content $OutFile ""
Add-Content $OutFile "GET /api/communities/1/posts"
(Invoke-RestMethod -Uri 'http://localhost:8080/api/communities/1/posts' -Method Get) | ConvertTo-Json -Depth 10 | Add-Content $OutFile
Add-Content $OutFile ""
Add-Content $OutFile "POST /api/communities/1/posts"
$pbody = @{author='Tester'; text='Hello from test'} | ConvertTo-Json
(Invoke-RestMethod -Uri 'http://localhost:8080/api/communities/1/posts' -Method Post -ContentType 'application/json' -Body $pbody) | ConvertTo-Json -Depth 10 | Add-Content $OutFile
Add-Content $OutFile ""
Add-Content $OutFile "GET /api/communities/1/posts (after post)"
(Invoke-RestMethod -Uri 'http://localhost:8080/api/communities/1/posts' -Method Get) | ConvertTo-Json -Depth 10 | Add-Content $OutFile
Add-Content $OutFile ""
Get-Content $OutFile
