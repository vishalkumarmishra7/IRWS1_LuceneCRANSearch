rm -rf trec_eval-9.0.7/searchResult/*
rm -rf LuceneSearch/searchResult/*


echo "<<< Running script... >>"
cd LuceneSearch/
mvn package
java -jar target/*.jar


echo "Copying result files to trec_eval..."
cp -r searchResult ../trec_eval-9.0.7/

echo "Running trec_eval..."
cd ../trec_eval-9.0.7/

make

FILES=searchResult/*
for f in $FILES
do
  echo "./trec_eval -m map test/QRelsCorrectedforTRECeval" $f
  ./trec_eval -m map test/QRelsCorrectedforTRECeval $f
  echo " "
  # take action on each file. $f store current file name
done

