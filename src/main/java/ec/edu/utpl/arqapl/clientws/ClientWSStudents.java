package ec.edu.utpl.arqapl.clientws;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Scanner;

public class ClientWSStudents {
    private static final String URL_BASE = "http://localhost:4567/%s";

    public static void main(String[] args) {
        var opc = "1";
        var lector = new Scanner(System.in);

        do {
            System.out.println("Menú de opciones:\n" +
                    "1. Listar\n" +
                    "2. Ver un estudiante\n" +
                    "3. Buscar\n" +
                    "4. Agregar\n" +
                    "5. Actualizar\n" +
                    "6. Borrar\n" +
                    "S. Salir.\n");

            System.out.print("Su selección: ");
            opc = lector.next().toUpperCase();

            if(!opc.equalsIgnoreCase("S")) {
                try {
                    switch (opc) {
                        case "1":
                            System.out.println("Listado de estudiantes:");
                            getStudents();
                            System.out.println();
                            break;
                        case "2":
                            System.out.print("Ingrese el índice del estudiante: ");
                            var pos = lector.nextInt();
                            getStudent(pos);
                            System.out.println();
                            break;
                        case "3":
                            System.out.print("Ingrese criterio de búsqueda [name, lastname, age]: ");
                            var field = lector.next();
                            System.out.print("Ingrese el valor: ");
                            var value = lector.next();
                            search(field, value);
                            System.out.println();
                            break;
                        case "4":
                            System.out.print("Ingrese el nombre: ");
                            var name = lector.next();
                            System.out.print("Ingrese el apellido: ");
                            var lastname = lector.next();
                            System.out.print("Ingrese la edad: ");
                            var age = lector.nextInt();
                            addStudent(name, lastname, age);
                            System.out.println();
                            break;
                        case "5":
                            System.out.print("Ingrese el índice: ");
                            pos = lector.nextInt();
                            System.out.print("Ingrese el nombre: ");
                            name = lector.next();
                            System.out.print("Ingrese el apellido: ");
                            lastname = lector.next();
                            System.out.print("Ingrese la edad: ");
                            age = lector.nextInt();
                            updateStudent(pos, name, lastname, age);
                            System.out.println();
                            break;
                        case "6":
                            System.out.print("Ingrese el índice del estudiante: ");
                            pos = lector.nextInt();
                            deleteStudent(pos);
                            System.out.println();
                            break;
                        default:
                            System.out.println("Error");
                    }
                } catch (UnirestException ure) {
                    ure.printStackTrace();
                }
            }

        } while (!opc.equalsIgnoreCase("S"));

    }

    private static void getStudents() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(String.format(URL_BASE, "students"))
                .header("accept", "application/json")
                .asJson();

        if(response.getStatus() == 200) {
            System.out.println(response.getBody().toString());
        }
    }

    private static void getStudent(int pos) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(String.format(URL_BASE, "student/{pos}"))
                .header("accept", "application/json")
                .routeParam("pos", String.valueOf(pos))
                .asJson();

        if(response.getStatus() == 200) {
            System.out.println(response.getBody().toString());
        }
    }

    private static void search(String field, String value) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(String.format(URL_BASE, "students/filter"))
                .header("accept", "application/json")
                .queryString(field, value)//http://localhost:4567/students/filter?name=A
                .asJson();
        if(response.getStatus() == 200) {
            System.out.println(response.getBody().toString());
        }
    }

    private static void addStudent(String name, String lastname, int age) throws UnirestException {
        HttpResponse<String> response = Unirest.post(String.format(URL_BASE, "student"))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(String.format("{\"name\" : \"%s\",\"lastName\" : \"%s\",\"age\" : %d}", name, lastname, age))
                .asString();

        if(response.getStatus() == 201) {
            System.out.println("Created");
        } else {
            System.out.println("Error");
        }
    }

    private static void updateStudent(int pos, String name, String lastname, int age) throws UnirestException {
        HttpResponse<String> response = Unirest.put(String.format(URL_BASE, "student/{pos}"))
                .header("accept", "application/json")
                .routeParam("pos", String.valueOf(pos))
                .body(String.format("{\"name\" : \"%s\",\"lastName\" : \"%s\",\"age\" : %d}", name, lastname, age))
                .asString();

        if(response.getStatus() == 204) {
            System.out.println("Updated");
        } else {
            System.out.println("Error");
        }
    }

    private static void deleteStudent(int pos) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.delete(String.format(URL_BASE, "student/{pos}"))
                .header("accept", "application/json")
                .routeParam("pos", String.valueOf(pos))
                .asJson();

        if(response.getStatus() == 204) {
            System.out.println("Deleted");
        } else {
            System.out.println("Error");
        }
    }

}
