package functions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TabulatedFunctions {
    // Приватное статическое поле фабрики с инициализацией по умолчанию
    private static TabulatedFunctionFactory factory = 
        new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();
    
    // Приватный конструктор чтобы нельзя было создать объект класса
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создать объект класса TabulatedFunctions");
    }
    
    // Метод для замены фабрики
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }
    
    // Три перегруженных метода создания табулированных функций
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }
    
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }
    
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }
    
    // Табулирует функцию на заданном отрезке с заданным количеством точек
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее двух");
        }
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        
        // Используем фабрику вместо явного создания
        return createTabulatedFunction(leftX, rightX, values);
    }
    
    // Вывод табулированной функции в байтовый поток
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        
        // Записываем количество точек
        dataOut.writeInt(function.getPointsCount());
        
        // Записываем координаты всех точек
        for (int i = 0; i < function.getPointsCount(); i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }
        
        // Не закрываем поток, чтобы вызывающий код мог продолжать использовать его
        dataOut.flush();
    }
    
    // Ввод табулированной функции из байтового потока
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        
        // Читаем количество точек
        int pointsCount = dataIn.readInt();
        
        // Читаем координаты точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
        
        // Используем фабрику вместо явного создания
        return createTabulatedFunction(points);
    }
    
    // Запись табулированной функции в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        BufferedWriter writer = new BufferedWriter(out);
        
        // Записываем количество точек
        writer.write(String.valueOf(function.getPointsCount()));
        writer.write(" ");
        
        // Записываем координаты всех точек через пробел
        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.write(String.valueOf(function.getPointX(i)));
            writer.write(" ");
            writer.write(String.valueOf(function.getPointY(i)));
            if (i < function.getPointsCount() - 1) {
                writer.write(" ");
            }
        }
        
        // Не закрываем поток, чтобы вызывающий код мог продолжать использовать его
        writer.flush();
    }
    
    // Чтение табулированной функции из символьного потока
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        
        // Читаем количество точек
        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Ожидалось число (количество точек)");
        }
        int pointsCount = (int) tokenizer.nval;
        
        // Читаем координаты точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалось число (координата X)");
            }
            double x = tokenizer.nval;
            
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалось число (координата Y)");
            }
            double y = tokenizer.nval;
            
            points[i] = new FunctionPoint(x, y);
        }
        
        // Используем фабрику вместо явного создания
        return createTabulatedFunction(points);
    }

        public static TabulatedFunction createTabulatedFunction(
            Class<?> clazz, double leftX, double rightX, int pointsCount) {
        // Проверяем, что класс реализует TabulatedFunction
        if (!TabulatedFunction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                "Класс " + clazz.getName() + " не реализует интерфейс TabulatedFunction");
        }
        
        try {
            // Находим конструктор с параметрами (double, double, int)
            Constructor<?> constructor = clazz.getConstructor(
                double.class, double.class, int.class);
            
            // Создаем объект
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);
            
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "Класс " + clazz.getName() + " не имеет конструктора с параметрами (double, double, int)", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                "Ошибка при создании объекта класса " + clazz.getName(), e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(
        Class<?> clazz, double leftX, double rightX, double[] values) {
    if (!TabulatedFunction.class.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException(
            "Класс " + clazz.getName() + " не реализует интерфейс TabulatedFunction");
    }
    
    try {
        // Находим конструктор с параметрами (double, double, double[])
        Constructor<?> constructor = clazz.getConstructor(
            double.class, double.class, double[].class);
        
        return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);
        
    } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException(
            "Класс " + clazz.getName() + " не имеет конструктора с параметрами (double, double, double[])", e);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new IllegalArgumentException(
            "Ошибка при создании объекта класса " + clazz.getName(), e);
    }
}
        public static TabulatedFunction createTabulatedFunction(
            Class<?> clazz, FunctionPoint[] points) {
        if (!TabulatedFunction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                "Класс " + clazz.getName() + " не реализует интерфейс TabulatedFunction");
        }
        
        try {
            // Находим конструктор с параметрами (FunctionPoint[])
            Constructor<?> constructor = clazz.getConstructor(FunctionPoint[].class);
            
            return (TabulatedFunction) constructor.newInstance((Object) points);
            
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "Класс " + clazz.getName() + " не имеет конструктора с параметрами (FunctionPoint[])", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                "Ошибка при создании объекта класса " + clazz.getName(), e);
        }
    }
    public static TabulatedFunction tabulate(
        Class<?> clazz, Function function, double leftX, double rightX, int pointsCount) {
    if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
        throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
    }
    
    if (pointsCount < 2) {
        throw new IllegalArgumentException("Количество точек должно быть не менее двух");
    }
    
    if (leftX >= rightX) {
        throw new IllegalArgumentException("Левая граница должна быть меньше правой");
    }
    
    double[] values = new double[pointsCount];
    double step = (rightX - leftX) / (pointsCount - 1);
    
    for (int i = 0; i < pointsCount; i++) {
        double x = leftX + i * step;
        values[i] = function.getFunctionValue(x);
    }
    
    // Используем рефлексивный метод создания
    return createTabulatedFunction(clazz, leftX, rightX, values);
}

}