import os

project_command = "javac C:\\Users\\DiunR\\OneDrive\\Radboud\\And\\Temporal-Max-Flow\\src\\src\\Main.java" # command to run
samples_dir = "C:\\Users\\DiunR\\OneDrive\\Radboud\\And\\Temporal-Max-Flow\\src\\samples-restocking" # location of samples

grade = 0

for i in range(1, 28):
    stream = os.popen(project_command + " <" + samples_dir + "/" + str(i) + ".in") # Run the project command with the corresponding in file as STDIN
    output = stream.read().strip()

    with open(samples_dir + "/" + str(i) + ".out", 'r') as f: # Open corresponding out file and read the first line
        correct_out = f.readline().strip()
        result = output == correct_out
        print("sample", i, " result =", result)
        if (result):
            grade += 1
        else:
            print("output =", output, "expected =" , correct_out)

print("Passed", grade, "test(s)")
grade = grade / 2.7
print("Grade:", grade.__round__(), "/ 10")