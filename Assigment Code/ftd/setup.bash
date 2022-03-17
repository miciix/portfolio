echo "================ Installing package for React =================="
npm install
echo "Done"
echo "======================== Done =================================="

echo "================ Installing package for Server ================="
cd server
npm install
echo "======================== Done =================================="

echo "================= Installing database schema ==================="
cd ..
psql "dbname='webdb' user='webdbuser' password='password' host='localhost'" -f db/schema.sql
echo "===================== Done ====================================="

echo "Running server and react app concurrently..."
npm run dev