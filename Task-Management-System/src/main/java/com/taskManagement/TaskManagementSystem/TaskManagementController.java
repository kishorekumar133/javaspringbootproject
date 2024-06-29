package com.taskManagement.TaskManagementSystem;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class TaskManagementController {
    String jdbcurl="jdbc:mysql://localhost:3306/task";
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/taskview")
    public String taskview() {
        return "Taskview";
    }

    @GetMapping("/signup")
    public String SignUp() {
        return "signup";
    }

    @GetMapping("/addtask")
    public String AddTask() {
        return "AddTask";
    }

    @GetMapping("/TaskView")
    public String submit(@RequestParam String userid, @RequestParam String password) {
        System.out.println("Inside submit method");
        try {
            Connection connection = DriverManager.getConnection(jdbcurl, "root", "Manoj@2004");
            PreparedStatement ps = connection.prepareStatement("SELECT password FROM user WHERE username = ?");
            ps.setString(1, userid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Check if there is a result in the ResultSet
                String dbPassword = rs.getString("password");
                System.out.println("The dbpass is " + dbPassword + " user pass is " + password);

                if (password.equals(dbPassword)) {
                    return "redirect:/taskview";
                } else {
                    System.out.println("Wrong credentials");
                    return "login"; // Return appropriate view for incorrect credentials
                }
            } else {
                System.out.println("User not found");
                return "login"; // Return login page if user not found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception here, maybe return an error page or log it
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions here, maybe return an error page or log it
        }

        return "login"; // Default return if something goes wrong
    }




    @PostMapping("/Create")
        public String createAcc(@RequestParam("firstName") String firstName
        , @RequestParam("lastName") String lastName
        , @RequestParam("email") String email
        , @RequestParam("mobileNumber") String mobileNumber
        , @RequestParam("address") String address
        , @RequestParam("username") String username
        , @RequestParam("password") String password){
    try{
        Connection connection =DriverManager.getConnection(jdbcurl,"root","Manoj@2004");
        String sql= "insert into user values(?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,username);
        ps.setString(2,firstName);
        ps.setString(3,lastName);
        ps.setString(4,email);
        ps.setString(5,mobileNumber);
        ps.setString(6,address);
        ps.setString(7,password);
        ps.execute();
        System.out.println("Database updated successfully");
        connection.close();
    }catch(Exception e){
        System.out.println("The exception occured is "+e);
    }
    return "login";
}



    @PostMapping("/addtask")
    public String addTask(    @RequestParam("name") String taskname,
                              @RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate,
                              @RequestParam("difficulty") String difficulty,
                              @RequestParam("priority") String priority
    ) {
        System.out.println("inside addtask");
        System.out.println("name: "+taskname+" startDate: "+startDate+" endDate: "+endDate+" difficulty: "+difficulty+" priority: "+priority);
        try {
            Connection connection = DriverManager.getConnection(jdbcurl, "root", "Manoj@2004");
            String sql = "INSERT INTO tasks (taskname, startDate, endDate, difficulty, priority) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1, taskname);
            pstatement.setString(2, startDate);
            pstatement.setString(3, endDate);
            pstatement.setString(4, difficulty);
            pstatement.setString(5, priority);
            pstatement.execute();
            System.out.println("Database updated successfully");
            connection.close();
        } catch (Exception e) {
            System.out.println("Exception is " + e);
        }
        return "redirect:/taskview";
    }



    private String message = "";
    @GetMapping("/message")
    public String showMessage(Model model) {
        model.addAttribute("message", message); // Add current message to model
        return "message";
    }
    @PostMapping("/delete")
    public String deleteTask(@RequestParam("taskId") String taskId ,Model model){
        System.out.println("taskId"+taskId);
        if(taskId == null){
            message = "Task ID is null!";
            model.addAttribute("message", message); // Update message in model
            return "redirect:/message";
        }
        try{
            Connection connection = DriverManager.getConnection(jdbcurl, "root", "Manoj@2004");
            String sql= "delete from tasks where taskname = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,taskId);
            ps.execute();
        }catch  (Exception e){
            System.out.println("Exception is "+e);

        }
        return "redirect:/taskview";
    }


    @GetMapping("/tasks")
    public String viewTasks(@RequestParam(name = "viewType", defaultValue = "table") String viewType, Model model) {
        List<Map<String,Object>> data = fetchTask();
        model.addAttribute("taskList", data);
        model.addAttribute("viewType", viewType);
        return "redirect:/taskview";
    }

        @ModelAttribute("taskList")
        public List<Map<String,Object>> fetchTask(){
            List<Map<String,Object>> listofftasks = new ArrayList<>();
            try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "Manoj@2004")) {
                String sql = "SELECT * FROM tasks";
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    Map<String,Object> task = new HashMap<>();
                    task.put("Task Name", rs.getString("taskname"));
                    task.put("Start Date", rs.getString("startdate"));
                    task.put("End Date", rs.getString("enddate"));
                    task.put("Priority", rs.getString("priority"));
                    task.put("Difficulty", rs.getString("difficulty"));
                    listofftasks.add(task);
                }
            } catch (SQLException e) {
                System.out.println("An exception occurred: " + e);
            }
            return listofftasks;
        }
}
