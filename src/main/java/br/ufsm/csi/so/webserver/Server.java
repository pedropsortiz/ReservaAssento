package br.ufsm.csi.so.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {

    private static final String HEADER = "HTTP/1.1 200 OK\n" + "Content-Type: mime; charset=UTF-8\n\n";
    private static final String HTML = "<html><head><title>Ola Mundo</title></head><body><h1>Ola Mundo!</h1></body></html>";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8088);
        System.out.println("Escutando a porta 8088...");

        while (true) {
            Socket socket = serverSocket.accept();
//          System.out.println("Conexão recebida de " + socket.getInetAddress());
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            int size = in.read(buffer);

            if (size > 0) {
                String req = new String(buffer, 0, size); //
                String[] linhas = req.split("\n");
                String[] linha0 = linhas[0].split(" ");
                String metodo = linha0[0];
                String recurso = linha0[1];

                if (metodo.equals("GET")) {
                    recurso = recurso.substring(1);
                    File file = new File("src\\main\\resources\\" + recurso);
                    if (file.exists()) {
                        Path path = file.toPath();
                        String mimeType = Files.probeContentType(path);
                        String head = HEADER;
                        if (mimeType != null) {
                            head = HEADER.replaceAll("mime", mimeType);
                        }
                        out.write(head.getBytes(StandardCharsets.UTF_8));
                        FileInputStream fin = new FileInputStream(file);
                        byte[] buf_arquivo = new byte[1024];
                        int read;

                        do {
                            read = fin.read(buf_arquivo);
                            if (read > 0) {
                                out.write(buf_arquivo, 0, read);
                            }
                        } while (read > 0);
                        fin.close();
                    } else {
                        out.write("HTTP/1.1 404 NOT FOUND\n\n".getBytes(StandardCharsets.UTF_8));
                    }
                }

                //Encerrando comunicação

            }
            in.close();
            out.close();
            socket.close();
        }

    }

}
