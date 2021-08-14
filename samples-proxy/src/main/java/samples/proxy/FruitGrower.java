package samples.proxy;

/**
 * 果农
 */
public class FruitGrower implements Sales{
    @Override
    public void sellFruit() {
        System.out.println("Successfully sold fruits.");
    }
}
