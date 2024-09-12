
Current dir:<>/216201004/2b
Step 1:
mkdir bin

Compile:
javac src/asn2b/*.java -d bin

Run:
java  -cp bin asn2b.Solution <input.txt

Input :
Subject ids for Btech are b1, b2..
Subject ids for Mtech are m1, m2..
Subject ids for Mtech are p1, p2..

third line ends with a -d to mark end of input.

output format:

A sample output which shows faults for every course, max issued subject id and books left afer simulation(first column is book_id, second is subject_id).

Total faults Btech:6
Total faults Mtech:4
Total faults PhD:5
Max issued course id for Btech:b1
Max issued course id for Mtech:m4
Max issued course id for PhD:p3
Btech books left after simulation:
b34 b3
b15 b1
b46 b4
Mtech books left after simulation:
m22 m2
m45 m4
m58 m5
Phd books left after simulation:
p23 p2
p32 p3
p44 p4


