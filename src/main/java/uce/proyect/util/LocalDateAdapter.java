package uce.proyect.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Clase que customiza las fechas de envio en el json ya que quier el formato "yyyy-MM-dd" y no fecha:[yyyy, MM, dd]
// Cuando creo el builder con gson debo de pasarle la clase o crear la implementacion de la interfaz director
public class LocalDateAdapter implements JsonSerializer<LocalDate> {
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
