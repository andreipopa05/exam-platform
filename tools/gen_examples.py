#!/usr/bin/env python3
"""Reference implementation of the exam platform, written from the assignment
statement (not from the Java code). It defines a few example scenarios and
writes, for each one, input.in and the expected output files.

Usage: python3 gen_examples.py <examples_root>
"""
import os
import sys
from datetime import datetime

FMT = "%d-%m-%Y-%H-%M"


def java_double(x):
    """Double.toString for the simple values used in the examples."""
    s = repr(float(x))
    return s


def money(x):
    # String.format("%.2f") in Java uses HALF_UP; the examples avoid exact ties
    return "%.2f" % x


class Question:
    def __init__(self, kind, author, difficulty, max_score, text, right, when):
        self.kind, self.author, self.difficulty = kind, author, difficulty
        self.max_score, self.text, self.right, self.when = max_score, text, right, when

    def correctness(self, answer):
        if self.kind == "multiple_choice":
            return "correct" if answer == self.right else "wrong"
        if answer == self.right:
            return "correct"
        n = len(self.right)
        if self.kind == "open_ended":
            low, high = int(0.7 * n), int(1.3 * n)
        else:  # fill_in_the_blank
            low, high = n - 2, n + 2
        if low <= len(answer) <= high and (self.right in answer or answer in self.right):
            return "partial"
        return "wrong"

    def percentage(self, correctness):
        if correctness == "correct":
            return 1.0
        if correctness == "partial":
            return {"open_ended": 0.7, "fill_in_the_blank": 0.5}.get(self.kind, 0.0)
        return 0.0

    def score(self, answer):
        return self.max_score * self.percentage(self.correctness(answer))


class Exam:
    def __init__(self, name, start, end):
        self.name, self.start, self.end = name, start, end
        self.questions = []
        self.scores = {}

    def history(self):
        qs = sorted(self.questions, key=lambda q: (q.when, q.author))  # stable
        return "".join(
            "%s | %s | %s | %d | %s | %s\n" % (
                q.when.strftime(FMT), q.text, q.right, q.difficulty,
                java_double(q.max_score), q.author) for q in qs)

    def print_order(self):
        return sorted(self.questions, key=lambda q: (q.difficulty, q.text))  # stable

    def printed(self):
        return "".join(
            "%s | %s | %d | %s\n" % (java_double(q.max_score), q.text, q.difficulty, q.right)
            for q in self.print_order())

    def report(self):
        return "".join("%s | %s\n" % (n, money(s)) for n, s in sorted(self.scores.items()))


class Platform:
    def __init__(self):
        self.exams, self.students = {}, {}
        self.files = {}      # file name -> content
        self.console = ""

    def run(self, lines):
        for line in lines:
            parts = line.split(" | ")
            ts = datetime.strptime(parts[0], FMT) if parts[1] != "exit" else None
            cmd = parts[1]
            if cmd == "exit":
                break
            if cmd == "create_exam":
                self.exams[parts[2]] = Exam(parts[2], datetime.strptime(parts[3], FMT),
                                            datetime.strptime(parts[4], FMT))
            elif cmd == "add_question":
                kind, exam, author, diff, mx, text, right = parts[2], parts[3], parts[4], int(parts[5]), float(parts[6]), parts[7], parts[8]
                self.exams[exam].questions.append(Question(kind, author, diff, mx, text, right, ts))
            elif cmd == "list_questions_history":
                self.files["questions_history_%s_%s.out" % (parts[2], parts[0])] = self.exams[parts[2]].history()
            elif cmd == "print_exam":
                self.files["print_exam_%s_%s.out" % (parts[2], parts[0])] = self.exams[parts[2]].printed()
            elif cmd == "register_student":
                self.students[parts[2]] = parts[3]
            elif cmd == "submit_exam":
                exam, student, answers = self.exams[parts[2]], parts[3], parts[4:]
                if ts < exam.start or ts > exam.end:
                    exam.scores[student] = 0.0
                    self.console += "%s | Submission outside of time interval for student %s\n" % (parts[0], student)
                else:
                    total = 0.0
                    for q, a in zip(exam.print_order(), answers):
                        total += q.score(a)
                    exam.scores[student] = total
            elif cmd == "show_student_score":
                exam = self.exams[parts[3]]
                self.console += "%s | The score of %s for exam %s is %s\n" % (
                    parts[0], parts[2], parts[3], money(exam.scores.get(parts[2], 0.0)))
            elif cmd == "generate_report":
                self.files["exam_report_%s_%s.out" % (parts[2], parts[0])] = self.exams[parts[2]].report()
        if self.console:
            self.files["console.out"] = self.console
        return self.files


EXAMPLES = {}

EXAMPLES["01-basics"] = """\
01-01-2025-09-00 | create_exam | Algorithms | 01-01-2025-10-00 | 01-01-2025-12-00
01-01-2025-09-05 | add_question | multiple_choice | Algorithms | Prof Ionescu | 3 | 10 | What is the complexity of binary search? A) O(n) B) O(log n) C) O(n log n) D) O(1) | B
01-01-2025-09-06 | add_question | open_ended | Algorithms | Prof Ionescu | 2 | 5 | What principle does a stack follow? | last in first out
01-01-2025-09-06 | add_question | fill_in_the_blank | Algorithms | Prof Dumitru | 2 | 5 | A queue follows the ___ principle | FIFO
01-01-2025-09-10 | list_questions_history | Algorithms
01-01-2025-09-15 | print_exam | Algorithms
01-01-2025-09-20 | register_student | Ana Popescu | 321CB
01-01-2025-09-20 | register_student | Bogdan Radu | 322CA
01-01-2025-09-20 | register_student | Carmen Ilie | 323AB
01-01-2025-09-20 | register_student | Dan Voinea | 324CC
01-01-2025-10-30 | submit_exam | Algorithms | Ana Popescu | FIFO | last in first out | B
01-01-2025-10-45 | submit_exam | Algorithms | Bogdan Radu | FIF | last in | A
01-01-2025-10-46 | submit_exam | Algorithms | Dan Voinea | FIFOS | the last in first out | B
01-01-2025-10-50 | show_student_score | Ana Popescu | Algorithms
01-01-2025-11-00 | generate_report | Algorithms
01-01-2025-12-30 | submit_exam | Algorithms | Carmen Ilie | FIFO | last in first out | B
01-01-2025-12-35 | show_student_score | Carmen Ilie | Algorithms
01-01-2025-12-40 | generate_report | Algorithms
01-01-2025-12-45 | exit
"""

EXAMPLES["02-questions-added-after-print"] = """\
02-02-2025-08-00 | create_exam | Networks | 02-02-2025-09-00 | 02-02-2025-11-00
02-02-2025-08-01 | add_question | multiple_choice | Networks | Prof Marin | 3 | 6 | Which layer routes packets? A) Link B) Network C) Transport D) Application | B
02-02-2025-08-02 | add_question | fill_in_the_blank | Networks | Prof Marin | 2 | 4 | TCP stands for Transmission Control ___ | Protocol
02-02-2025-08-10 | print_exam | Networks
02-02-2025-08-12 | register_student | Elena Stan | 321CB
02-02-2025-08-15 | add_question | open_ended | Networks | Prof Marin | 1 | 10 | Explain what DNS does. | translates names to IP addresses
02-02-2025-09-30 | submit_exam | Networks | Elena Stan | translates names to IP addresses | Protocol | B
02-02-2025-09-35 | show_student_score | Elena Stan | Networks
02-02-2025-09-40 | generate_report | Networks
02-02-2025-09-45 | exit
"""

EXAMPLES["03-ordering-and-two-exams"] = """\
10-03-2025-07-00 | create_exam | Math | 10-03-2025-09-00 | 10-03-2025-11-00
10-03-2025-07-01 | create_exam | Physics | 10-03-2025-10-00 | 10-03-2025-12-00
10-03-2025-07-55 | add_question | fill_in_the_blank | Math | Mircea Dan | 5 | 2.5 | The sum of angles in a triangle is ___ degrees | 180
10-03-2025-08-00 | add_question | open_ended | Math | Zoe Pop | 2 | 4 | Name the derivative of x squared | 2x
10-03-2025-08-00 | add_question | multiple_choice | Math | Ana Ban | 2 | 4 | Is 7 prime? A) yes B) no C) maybe D) unknown | A
10-03-2025-08-01 | add_question | multiple_choice | Physics | Ana Ban | 1 | 3 | Unit of force? A) Joule B) Newton C) Watt D) Pascal | B
10-03-2025-08-01 | add_question | open_ended | Physics | Ana Ban | 1 | 3 | Name the unit of energy | joule
10-03-2025-08-30 | list_questions_history | Math
10-03-2025-08-31 | print_exam | Math
10-03-2025-08-32 | print_exam | Physics
10-03-2025-08-40 | register_student | Zoe Pop | 331AA
10-03-2025-08-40 | register_student | Ana Ban | 332BB
10-03-2025-08-41 | register_student | Mihai Rus | 333CC
10-03-2025-09-30 | submit_exam | Math | Zoe Pop | A | 2x | 18
10-03-2025-09-31 | generate_report | Math
10-03-2025-09-40 | submit_exam | Physics | Zoe Pop | joule | B
10-03-2025-09-50 | submit_exam | Math | Ana Ban | B | x | 180
10-03-2025-10-30 | submit_exam | Physics | Mihai Rus | joules | A
10-03-2025-10-31 | submit_exam | Math | Mihai Rus | A | 2x | 180
10-03-2025-10-45 | show_student_score | Zoe Pop | Physics
10-03-2025-10-46 | show_student_score | Zoe Pop | Math
10-03-2025-10-47 | show_student_score | Mihai Rus | Math
10-03-2025-10-50 | generate_report | Math
10-03-2025-10-51 | generate_report | Physics
10-03-2025-10-55 | exit
"""


def main():
    root = sys.argv[1]
    for name, text in EXAMPLES.items():
        directory = os.path.join(root, name)
        expected = os.path.join(directory, "expected")
        os.makedirs(expected, exist_ok=True)
        with open(os.path.join(directory, "input.in"), "w", encoding="utf-8", newline="\n") as f:
            f.write(text)
        files = Platform().run(text.strip("\n").split("\n"))
        for fname, content in files.items():
            with open(os.path.join(expected, fname), "w", encoding="utf-8", newline="\n") as f:
                f.write(content)
        print(name, "->", sorted(files))


if __name__ == "__main__":
    main()
