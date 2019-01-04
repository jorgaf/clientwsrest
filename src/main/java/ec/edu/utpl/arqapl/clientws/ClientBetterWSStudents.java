package ec.edu.utpl.arqapl.clientws;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import ec.edu.utpl.arqapl.clientws.model.Student;

import java.io.IOException;
import java.util.*;

public class ClientBetterWSStudents {
    private static final String URL_BASE = "http://localhost:4567/%s";

    public static void main(String[] args) throws IOException {
        var opc = "1";
        var lector = new Scanner(System.in);

        config();

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
                            printList(getStudents());
                            System.out.println();
                            break;
                        case "2":
                            System.out.print("Ingrese el índice del estudiante: ");
                            var pos = lector.nextInt();
                            getStudent(pos).ifPresent(System.out::println);
                            System.out.println();
                            break;
                        case "3":
                            System.out.print("Ingrese criterio de búsqueda [name, lastname, age]: ");
                            var field = lector.next();
                            System.out.print("Ingrese el valor: ");
                            var value = lector.next();
                            printList(search(field, value));
                            System.out.println();
                            break;
                        case "4":
                            System.out.print("Ingrese el nombre: ");
                            var name = lector.next();
                            System.out.print("Ingrese el apellido: ");
                            var lastname = lector.next();
                            System.out.print("Ingrese la edad: ");
                            var age = lector.nextInt();
                            addStudent(new Student(name, lastname, age));
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
                            updateStudent(pos, new Student(name, lastname, age));
                            System.out.println();
                            break;
                        case "6":
                            System.out.print("Ingrese el índice del estudiante: ");
                            pos = lector.nextInt();
                            if(deleteStudent(pos)) {
                                System.out.println("Deleted");
                            } else {
                                System.out.println("Error");
                            }
                            System.out.println();
                            break;
                        default:
                            System.out.println("Error");
                    }
                } catch (UnirestException ure) {
                    ure.printStackTrace();
                }
            } else {
                Unirest.shutdown();
            }

        } while (!opc.equalsIgnoreCase("S"));

    }

    private static List<Student> getStudents() throws UnirestException {
        HttpResponse<List> response = Unirest.get(String.format(URL_BASE, "/students"))
                .header("accept", "application/json")
                .asObject(List.class);

        if(response.getStatus() == 200) {
            return response.getBody();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static Optional<Student> getStudent(int pos) throws UnirestException {
        HttpResponse<Student> response = Unirest.get(String.format(URL_BASE, "student/{pos}"))
                .header("accept", "application/json")
                .routeParam("pos", String.valueOf(pos))
                .asObject(Student.class);

        if(response.getStatus() == 200) {
            return Optional.of(response.getBody());
        }
        return Optional.empty();
    }

    private static List<Student> search(String field, String value) throws UnirestException {
        HttpResponse<List> response = Unirest.get(String.format(URL_BASE, "students/filter"))
                .header("accept", "application/json")
                .queryString(field, value)
                .asObject(List.class);

        if(response.getStatus() == 200) {
            return response.getBody();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static void addStudent(Student student) throws UnirestException {
        HttpResponse<String> response = Unirest.post(String.format(URL_BASE, "student"))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(student)
                .asString();

        if(response.getStatus() == 201) {
            System.out.println("Created");
        } else {
            System.out.println("Error");
        }
    }

    private static void updateStudent(int pos, Student student) throws UnirestException {
        HttpResponse<String> response = Unirest.put(String.format(URL_BASE, "student/{pos}"))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .routeParam("pos", String.valueOf(pos))
                .body(student)
                .asString();

        if(response.getStatus() == 204) {
            System.out.println("Updated");
        } else {
            System.out.println("Error");
        }
    }

    private static boolean deleteStudent(int pos) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.delete(String.format(URL_BASE, "student/{pos}"))
                .header("accept", "application/json")
                .routeParam("pos", String.valueOf(pos))
                .asJson();

         return (response.getStatus() == 204);
    }

    private static void config() {

        Unirest.setObjectMapper(new ObjectMapper() {
            private Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            @Override
            public <T> T readValue(String s, Class<T> aClass) {

                if(parser.parse(s).isJsonArray()) {
                    return gson.fromJson(s, new TypeToken<List<Student>>() {
                    }.getType());
                } else {
                    return gson.fromJson(s, aClass);
                }
            }

            @Override
            public String writeValue(Object o) {
                return gson.toJson(o);
            }
        });
    }

    private static void printList(List<Student> list) {
        list.forEach(System.out::println);
    }

}
