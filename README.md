# Jayson
Jayson is a Java implementation of a JSON parser in Java. With a stupid name.

Why? Because it's funny. And I was bored.

### Features
#### Tokenizer
A (hopefully) fully JSON spec compliant tokenizer. Splits the JSON data into a token stream.
Can not currently be used independantly of the parser, because I forgot to add that.
Might do that sometime else.

#### Parser
Parses the token stream into Java objects. Quite literally return an Object.
The data is structured into Objects (with a map for key/value pairs), Lists (for arrays)
and the usual stuff such as Numbers (split in Double, Float and BigInteger), Booleans,
Null values. And cool stuff like numbers with fractals or scientific e-notation are supported,
same as Unicode escape sequences (yes, surrogate pairs as well, I don't like them however).

#### Jayson 
The wrapper class to use this thing (which no-one will ever do, probably).
Has two methods for parsing, one accepting a String and another one accepting a File input.
