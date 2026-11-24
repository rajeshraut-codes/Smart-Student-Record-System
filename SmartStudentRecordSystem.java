import java.io.*;
import java.util.Scanner;

/**
 * Smart Student Record System (PS No. 51)
 * Concepts: Array, Linked List, BST, Merge Sort, Searching, Sorting, File Handling
 *
 * Save this file as SmartStudentRecordSystem.java (name must match exactly).
 * Compile:  javac SmartStudentRecordSystem.java
 * Run:      java SmartStudentRecordSystem
 */
public class SmartStudentRecordSystem {

    // ---------- Student class ----------
    static class Student {
        String id;
        String name;
        int marks;

        Student(String id, String name, int marks) {
            this.id = id;
            this.name = name;
            this.marks = marks;
        }

        @Override
        public String toString() {
            return id + " | " + name + " | " + marks;
        }
    }

    // ---------- Linked list node ----------
    static class ListNode {
        Student data;
        ListNode next;

        ListNode(Student data) {
            this.data = data;
        }
    }

    // ---------- BST node (keyed by student name) ----------
    static class BSTNode {
        Student data;
        BSTNode left, right;

        BSTNode(Student data) {
            this.data = data;
        }
    }

    private static final String FILE_NAME = "student_records.txt";
    private static final int FIRST_ID = 101;

    private ListNode head = null;   // linked list of all students
    private BSTNode root = null;    // BST for name-based lookup
    private int nextIdNumber = FIRST_ID;
    private int count = 0;

    // ===== Linked list operations =====
    private boolean idExists(String id) {
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.data.id.equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    private void appendToList(Student s) {
        ListNode node = new ListNode(s);
        if (head == null) {
            head = node;
        } else {
            ListNode cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = node;
        }
        count++;
    }

    private Student findById(String id) {
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.data.id.equalsIgnoreCase(id)) return cur.data;
        }
        return null;
    }

    // ===== BST operations =====
    private BSTNode insertBST(BSTNode node, Student s) {
        if (node == null) return new BSTNode(s);
        if (s.name.compareToIgnoreCase(node.data.name) < 0)
            node.left = insertBST(node.left, s);
        else
            node.right = insertBST(node.right, s);
        return node;
    }

    private Student searchBST(BSTNode node, String name) {
        if (node == null) return null;
        int cmp = name.compareToIgnoreCase(node.data.name);
        if (cmp == 0) return node.data;
        return cmp < 0 ? searchBST(node.left, name) : searchBST(node.right, name);
    }

    private void rebuildBST() {
        root = null;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            root = insertBST(root, cur.data);
        }
    }

    // ===== Merge sort (descending by marks) on an array =====
    private void mergeSort(Student[] arr, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void merge(Student[] arr, int left, int mid, int right) {
        Student[] temp = new Student[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) {
            if (arr[i].marks >= arr[j].marks) temp[k++] = arr[i++];
            else temp[k++] = arr[j++];
        }
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];
        for (k = 0; k < temp.length; k++) arr[left + k] = temp[k];
    }

    // ===== Features =====
    public void addStudent(String name, int marks) {
        name = (name == null) ? "" : name.replace(",", " ").trim();  // commas would break the file format
        if (name.isEmpty()) {
            System.out.println("Invalid name.");
            return;
        }
        if (marks < 0 || marks > 100) {
            System.out.println("Invalid marks. Marks must be between 0 and 100.");
            return;
        }
        String id = "S" + nextIdNumber;
        if (idExists(id)) {   // duplicate ID check
            System.out.println("Duplicate ID rejected: " + id);
            return;
        }
        nextIdNumber++;
        Student s = new Student(id, name, marks);
        appendToList(s);
        root = insertBST(root, s);
        System.out.println("Student added. ID: " + s);
    }

    public void displayAll() {
        if (head == null) {
            System.out.println("No records found.");
            return;
        }
        System.out.println("ID | Name | Marks");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            System.out.println(cur.data);
        }
    }

    public void searchById(String id) {
        Student s = findById(id);
        if (s == null) System.out.println("Student " + id + " not found.");
        else System.out.println(s);
    }

    public void searchByName(String name) {
        Student s = searchBST(root, name);
        if (s == null) System.out.println("Student " + name + " not found.");
        else System.out.println(s);
    }

    public void deleteStudent(String id) {
        if (head == null) {
            System.out.println("Student " + id + " not found.");
            return;
        }
        if (head.data.id.equalsIgnoreCase(id)) {
            head = head.next;
        } else {
            ListNode cur = head;
            while (cur.next != null && !cur.next.data.id.equalsIgnoreCase(id)) cur = cur.next;
            if (cur.next == null) {
                System.out.println("Student " + id + " not found.");
                return;
            }
            cur.next = cur.next.next;
        }
        count--;
        rebuildBST();
        System.out.println("Student " + id + " deleted.");
    }

    public void showRankList() {
        if (count == 0) {
            System.out.println("No records found.");
            return;
        }
        Student[] arr = new Student[count];
        int i = 0;
        for (ListNode cur = head; cur != null; cur = cur.next) arr[i++] = cur.data;
        mergeSort(arr, 0, arr.length - 1);
        System.out.println("Rank list:");
        for (int r = 0; r < arr.length; r++) {
            System.out.println((r + 1) + ". " + arr[r]);
        }
    }

    // ===== File handling =====
    public void loadFromFile() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length != 3) continue;
                String id = p[0].trim();
                String name = p[1].trim();
                int marks;
                try {
                    marks = Integer.parseInt(p[2].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                if (id.length() < 2 || name.isEmpty()) continue;
                if (marks < 0 || marks > 100 || idExists(id)) continue; // reject bad data
                Student s = new Student(id, name, marks);
                appendToList(s);
                root = insertBST(root, s);
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num >= nextIdNumber) nextIdNumber = num + 1;
                } catch (NumberFormatException ignored) { }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (ListNode cur = head; cur != null; cur = cur.next) {
                pw.println(cur.data.id + "," + cur.data.name + "," + cur.data.marks);
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // ===== Menu =====
    public static void main(String[] args) {
        SmartStudentRecordSystem sys = new SmartStudentRecordSystem();
        sys.loadFromFile();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== Smart Student Record System =====");
            System.out.println("1. Add student");
            System.out.println("2. Display all students");
            System.out.println("3. Search by ID");
            System.out.println("4. Search by name (BST)");
            System.out.println("5. Delete student");
            System.out.println("6. Rank list (sorted by marks)");
            System.out.println("7. Save and exit");
            System.out.print("Enter choice: ");
            if (!sc.hasNextLine()) {   // input ended: save and stop instead of crashing
                sys.saveToFile();
                break;
            }
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": {
                    System.out.print("Name: ");
                    String name = sc.hasNextLine() ? sc.nextLine() : "";
                    System.out.print("Marks (0-100): ");
                    try {
                        int marks = Integer.parseInt(sc.hasNextLine() ? sc.nextLine().trim() : "");
                        sys.addStudent(name, marks);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid marks. Please enter a number.");
                    }
                    break;
                }
                case "2":
                    sys.displayAll();
                    break;
                case "3":
                    System.out.print("Enter ID: ");
                    sys.searchById(sc.hasNextLine() ? sc.nextLine().trim() : "");
                    break;
                case "4":
                    System.out.print("Enter name: ");
                    sys.searchByName(sc.hasNextLine() ? sc.nextLine().trim() : "");
                    break;
                case "5":
                    System.out.print("Enter ID to delete: ");
                    sys.deleteStudent(sc.hasNextLine() ? sc.nextLine().trim() : "");
                    break;
                case "6":
                    sys.showRankList();
                    break;
                case "7":
                    sys.saveToFile();
                    System.out.println("Records saved to " + FILE_NAME + ". Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        sc.close();
    }
}
