package samples.proxy;


/**
 * 水果代理商、经销商
 */
public class Dealer implements Sales{
    private FruitGrower fruitGrower;

    public Dealer(FruitGrower fruitGrower) {
        this.fruitGrower = fruitGrower;
    }

    @Override
    public void sellFruit() {
        if (fruitGrower == null){
            this.fruitGrower = new FruitGrower();
        }
        // 售卖前涨价
        fruitGrower.sellFruit();
        // 售卖后处理
    }
}
