package ru.astonddevs;

import ru.astonddevs.dao.UserDao;
import ru.astonddevs.dao.UserDaoImpl;
import ru.astonddevs.dto.UserDTO;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
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
                    UserDTO userDTO = new UserDTO(name, email, age);
                    userDao.save(userDTO);
                    System.out.println("User created.");
                }
                case 2 -> {
                    List<UserDTO> users = userDao.getAll();
                    users.forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("Enter ID: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    System.out.println(userDao.getById(id));
                }
                case 4 -> {
                    System.out.print("Enter ID to update: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    UserDTO updatedUserDTO = userDao.getById(id);
                    if (updatedUserDTO == null) {
                        System.out.println("User not found.");
                        break;
                    }
                    System.out.print("New name: ");
                    updatedUserDTO.setName(scanner.nextLine());
                    System.out.print("New email: ");
                    updatedUserDTO.setEmail(scanner.nextLine());
                    System.out.print("New age: ");
                    updatedUserDTO.setAge(Integer.parseInt(scanner.nextLine()));
                    userDao.update(id, updatedUserDTO);
                    System.out.println("User updated.");
                }
                case 5 -> {
                    System.out.print("Enter ID to delete: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    userDao.delete(id);
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