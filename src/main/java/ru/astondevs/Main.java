package ru.astondevs;

import ru.astondevs.dao.UserDao;
import ru.astondevs.dao.UserDaoImpl;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.service.UserService;
import ru.astondevs.util.HibernateUtil;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl(HibernateUtil.getSessionFactory());
        UserService userService = new UserService(userDao);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    1. Create user
                    2. List users
                    3. Get user by ID
                    4. Update user
                    5. Delete user
                    0. Exit
                    """);
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> {
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Age: ");
                    int age = Integer.parseInt(scanner.nextLine());
                    userService.saveUser(name, email, age);
                    System.out.println("User created.");
                }
                case 2 -> {
                    List<UserDTO> users = userService.getAllUsers();
                    users.forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("Enter ID: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    System.out.println(userService.getUserById(id));
                }
                case 4 -> {
                    System.out.print("Enter ID to update: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    UserDTO currentUserDTO = userService.getUserById(id);
                    if (currentUserDTO == null) {
                        System.out.println("User not found.");
                        break;
                    }
                    System.out.print("New name: ");
                    String newName = scanner.nextLine();
                    System.out.print("New email: ");
                    String newEmail = scanner.nextLine();
                    System.out.print("New age: ");
                    int newAge = Integer.parseInt(scanner.nextLine());
                    userService.updateUser(currentUserDTO.getId(), newName, newEmail, newAge);
                    System.out.println("User updated.");
                }
                case 5 -> {
                    System.out.print("Enter ID to delete: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    userService.deleteUser(id);
                    System.out.println("User deleted.");
                }
                case 0 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}