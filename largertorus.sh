#!/bin/bash
file="largertorus"

printf "" > $file
printf "%d\n" 0 >> $file;
for i in $( seq 1 $(($1-2)) ); do
printf "%d %d:0.25\n" $i $((i-1)) >> $file;
done
printf "%d %d:0.25 %d:0.25\n" $(($1-1)) $(($1-2)) 0 >> $file;

for j in $( seq 1 $(($2-2)) ); do
printf "%d %d:1\n" $(($j*$1)) $(($j*$1-$1)) >> $file;
for i in $( seq 1 $(($1-2)) ); do
printf "%d %d:0.25 %d:1\n" $(($j*$1+$i)) $(($j*$1+$i-1)) $(($j*$1+$i-$1)) >> $file;
done
printf "%d %d:0.25 %d:0.25 %d:0.01\n" $(($j*$1+$1-1)) $(($j*$1+$1-2)) $(($j*$1)) $(($j*$1-1)) >> $file;
done

printf "%d %d:1 %d:1\n" $(($2*$1-$1)) $(($2*$1-$1-$1)) 0 >> $file;
for i in $( seq 1 $(($1-2)) ); do
printf "%d %d:0.25 %d:1 %d:1\n" $(($2*$1-$1+$i)) $(($2*$1-$1+$i-1)) $(($2*$1-$1+$i-$1)) $i >> $file;
done
printf "%d %d:0.25 %d:0.25 %d:0.01 %d:0.01\n" $(($2*$1-1)) $(($2*$1-2)) $(($2*$1-$1)) $(($2*$1-$1-1)) $(($1-1)) >> $file;
