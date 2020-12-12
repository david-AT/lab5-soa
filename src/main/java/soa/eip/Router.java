package soa.eip;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .log("Searching twitter for \"${body}\"!")
      
      // Interceptar mensaje antes de enviarlo a la API de Twitter
      .process(exchange -> {
          // Obtener contenido del mensaje
          String body = exchange.getIn().getBody(String.class);
          // Detectar número max mediante patrones.
          Pattern regex = Pattern.compile("max:(\\d+)");
          Matcher m = regex.matcher(body);
          // Patrón detectado
          if (m.find()) {
              int maxNum = Integer.parseInt(m.group(1));
              // Quitar del mensaje el string "max:N"
              body = body.replace("max:" + maxNum,"").trim();
              // Añadir como parámetro el número max.
              body += "?count=" + maxNum;
              // Actualizar contenido del mensaje
              exchange.getOut().setBody(body);
          }
      })
      .toD("twitter-search:${body}")
      .log("Body now contains the response from twitter:\n${body}");
  }
}
