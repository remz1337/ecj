@echo off



Rem #read rna from stdin and put in file, FASTA format for ViennaRNA RNAfold
rem echo %1 > rna_sequence.fa

set inputfile=%1
set target=%2


Rem #execute rnafold
RNAfold --noPS < %inputfile% > rna_sequence.out

rem call c++ exe
C:\Users\remib\source\repos\private\VRNA\out\build\x64-Release\VRNA.exe -f rna_sequence.out -t %target%

Rem #parse output
rem FOR /F "tokens=1" %%i IN (rna_sequence.out) DO set dotbracket=%%i & echo %dotbracket%

Rem #echo dotbracket notation
rem echo %dotbracket%