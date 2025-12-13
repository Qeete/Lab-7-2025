import functions.*;
import functions.basic.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ЛАБОРАТОРНАЯ РАБОТА №7 ===");
    testReflection();
    testFactory();
    testIterators();
    }
    public static void testReflection() {
        System.out.println("=== Тестирование рефлексивного создания объектов ===");
        
        TabulatedFunction f;
        
        // Тест 1: Создание ArrayTabulatedFunction через границы и количество точек
        System.out.println("\n1. ArrayTabulatedFunction через границы и количество точек:");
        f = TabulatedFunctions.createTabulatedFunction(
            ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println("   Тип: " + f.getClass().getSimpleName());
        System.out.println("   Функция: " + f);
        
        // Тест 2: Создание ArrayTabulatedFunction через границы и значения
        System.out.println("\n2. ArrayTabulatedFunction через границы и значения:");
        f = TabulatedFunctions.createTabulatedFunction(
            ArrayTabulatedFunction.class, 0, 10, new double[] {0, 5, 10});
        System.out.println("   Тип: " + f.getClass().getSimpleName());
        System.out.println("   Функция: " + f);
        
        // Тест 3: Создание LinkedListTabulatedFunction через массив точек
        System.out.println("\n3. LinkedListTabulatedFunction через массив точек:");
        f = TabulatedFunctions.createTabulatedFunction(
            LinkedListTabulatedFunction.class, 
            new FunctionPoint[] {
                new FunctionPoint(0, 0),
                new FunctionPoint(5, 25),
                new FunctionPoint(10, 100)
            }
        );
        System.out.println("   Тип: " + f.getClass().getSimpleName());
        System.out.println("   Функция: " + f);
        
        // Тест 4: Табулирование с рефлексивным созданием
        System.out.println("\n4. Табулирование Sin с LinkedListTabulatedFunction:");
        f = TabulatedFunctions.tabulate(
            LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 5);
        System.out.println("   Тип: " + f.getClass().getSimpleName());
        System.out.println("   Функция: " + f);
        
        // Тест 5: Ошибочный случай - класс не реализует TabulatedFunction
        System.out.println("\n5. Тест ошибки (класс не реализует TabulatedFunction):");
        try {
            f = TabulatedFunctions.createTabulatedFunction(
                String.class, 0, 10, 3);
        } catch (IllegalArgumentException e) {
            System.out.println("   Ожидаемая ошибка: " + e.getMessage());
        }
        
    }

    public static void testFactory() {
        System.out.println("=== Тестирование фабрик табулированных функций ===");
        
        Function f = new Cos();
        TabulatedFunction tf;
        
        // Тестируем фабрику по умолчанию (должна быть ArrayTabulatedFunction)
        System.out.println("\n1. Фабрика по умолчанию:");
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   Тип: " + tf.getClass().getSimpleName());
        
        // Меняем на LinkedList фабрику
        System.out.println("\n2. LinkedList фабрика:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   Тип: " + tf.getClass().getSimpleName());
        
        // Возвращаем Array фабрику
        System.out.println("\n3. Array фабрика:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   Тип: " + tf.getClass().getSimpleName());
        
        // Дополнительное тестирование других методов создания
        System.out.println("\n4. Тестирование разных методов создания:");
        
        // Тест создания через границы и количество точек
        TabulatedFunction tf1 = TabulatedFunctions.createTabulatedFunction(0, 10, 5);
        System.out.println("   create(0, 10, 5): " + tf1.getClass().getSimpleName());
        
        // Тест создания через границы и значения
        double[] values = {1, 2, 3, 4, 5};
        TabulatedFunction tf2 = TabulatedFunctions.createTabulatedFunction(0, 10, values);
        System.out.println("   create(0, 10, values): " + tf2.getClass().getSimpleName());
        
        // Тест создания через массив точек
        FunctionPoint[] points = {
            new FunctionPoint(0, 1),
            new FunctionPoint(2, 3), 
            new FunctionPoint(4, 5)
        };
        TabulatedFunction tf3 = TabulatedFunctions.createTabulatedFunction(points);
        System.out.println("   create(points): " + tf3.getClass().getSimpleName());
    }

    public static void testIterators() {
        System.out.println("=== Тестирование итератора ===");
        System.out.println("\nПроверка итератора ArrayTabulatedFunction:");
        TabulatedFunction func1 = new ArrayTabulatedFunction(
            new FunctionPoint[]{
                new FunctionPoint(0, 0), 
                new FunctionPoint(1, 1), 
                new FunctionPoint(2, 4)
            });
        for (FunctionPoint p : func1) {
            System.out.println(p);
        }
        
        System.out.println("\nПроверка итератора LinkedListTabulatedFunction:");
        TabulatedFunction func2 = new LinkedListTabulatedFunction(
            new FunctionPoint[]{
                new FunctionPoint(0, 0), 
                new FunctionPoint(1, 1), 
                new FunctionPoint(2, 4)
            });
        for (FunctionPoint p : func2) {
            System.out.println(p);
        }
    }
}