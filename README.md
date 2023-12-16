# swagger-sealed-extension
Библиотека - дополнение для swagger библиотеки.

Библиотека позволяет для sealed классов автоматически добавить в описание для раздела `oneOf` permitted классы.

## Использование
Чтобы использовать библиотеку, достаточно подключить её в зависимости проекта.

```xml
<dependency>
    <groupId>com.kuldiegor</groupId>
    <artifactId>swagger-sealed-extension-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Пример использования
Например у нас есть поле в неком классе
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Schema(description = "")
    private Pet pet;
}
```
И следующие классы
```java
public abstract sealed class Pet permits Cat, Dog{
    public static final int DOG_TYPE = 1;
    public static final int CAT_TYPE = 2;
}
```
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Cat")
public sealed class Cat extends Pet permits Kitty{
    private Integer type;

    @NotNull
    private Integer age;

    private String name;
}
```
```java
@Schema(description = "Kitty")
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Kitty extends Cat {
    private String cuteName;
}
```
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dog")
public final class Dog extends Pet{
    private Integer type;

    @NotNull
    @NotBlank
    private String name;
}
```

Документация для класса `Request` сформируется следующим образом

```json
{
  "Request": {
    "required": [
      "pet"
    ],
    "type": "object",
    "properties": {
      "pet": {
        "type": "object",
        "oneOf": [
          {
            "required": [
              "age"
            ],
            "type": "object",
            "properties": {
              "type": {
                "type": "integer",
                "format": "int32"
              },
              "age": {
                "type": "integer",
                "format": "int32"
              },
              "name": {
                "type": "string"
              }
            },
            "description": "Cat"
          },
          {
            "required": [
              "age"
            ],
            "type": "object",
            "properties": {
              "type": {
                "type": "integer",
                "format": "int32"
              },
              "age": {
                "type": "integer",
                "format": "int32"
              },
              "name": {
                "type": "string"
              },
              "cute_name": {
                "type": "string"
              }
            },
            "description": "Kitty"
          },
          {
            "required": [
              "name"
            ],
            "type": "object",
            "properties": {
              "type": {
                "type": "integer",
                "format": "int32"
              },
              "name": {
                "type": "string"
              }
            },
            "description": "Dog"
          }
        ]
      }
    }
  }
}
```

Теперь для поля `pet` будут отображаться `3` возможных класса `Cat`,`Dog`,`Kitty`. Для класса `Pet` в permitted есть `Cat` и `Dog`, плюс для `Cat` есть permitted класс `Kitty`,