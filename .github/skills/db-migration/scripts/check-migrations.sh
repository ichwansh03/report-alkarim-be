#!/bin/bash
# Migration Status Checker
# Usage: ./scripts/check-migrations.sh [info|pending|history]

set -e

command="${1:-info}"

echo "🔍 Checking Flyway migration status..."
echo ""

case "$command" in
  info)
    echo "📋 Migration Summary (flyway:info):"
    ./mvnw flyway:info -q
    ;;
  
  pending)
    echo "⏳ Checking for pending migrations..."
    ./mvnw flyway:info -q | grep PENDING || echo "✓ No pending migrations"
    ;;
  
  history)
    echo "📜 Full Migration History (last 10):"
    echo "Run this SQL query to view details:"
    cat <<'EOF'
SELECT installed_rank, version, description, type, installed_on, execution_time, success 
FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 10;
EOF
    ;;
  
  *)
    echo "Usage: ./scripts/check-migrations.sh [info|pending|history]"
    exit 1
    ;;
esac

echo ""
echo "✓ Done"
