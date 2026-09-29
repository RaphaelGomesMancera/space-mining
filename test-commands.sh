#!/usr/bin/env bash
# Envia os 6 comandos válidos, 1 inválido e uma rajada de 10 comandos.
URL="http://localhost:8080/command"

send () {
  code=$(curl -s -o /tmp/resp.json -w "%{http_code}" -X POST "$URL" \
    -H "Content-Type: application/json" -d "{\"command\": \"$1\"}")
  echo "$1 -> HTTP $code $(cat /tmp/resp.json)"
}

echo "== Comandos válidos =="
for c in RIGHT LEFT FRONT BACK OPEN CLOSE; do send "$c"; done

echo "== Comando inválido (espera 400) =="
send JUMP

echo "== Rajada (backpressure: o robô processa 1 por vez) =="
for i in $(seq 1 10); do send FRONT & done
wait

sleep 5
echo "== Contagem no banco =="
curl -s http://localhost:8082/commands; echo
