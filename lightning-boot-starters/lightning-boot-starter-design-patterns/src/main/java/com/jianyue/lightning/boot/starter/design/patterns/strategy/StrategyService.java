package com.jianyue.lightning.boot.starter.design.patterns.strategy;

/**
 * @author FLJ
 * @dateTime 2022/1/25 10:07
 * @description 策略服务..
 * 策略服务顶级接口....
 * @apiNote  当前策略服务存在一个小的缺陷, 本质上可以通过实现自己的自定义自动装配注解,类似于@AutoWired 形式来解决此问题,但是没必要
 * // 我们仅仅需要在实现此策略服务且目标接口T 在作为一个Spring bean的同时,任意一个bean 上标注好 @Autowired自动装配的名字(注意,仅仅是一个..)
 * 这算是一个api 约定.
 * 使用例子:
 * <pre>
 *     // 例如这里有一个 支付方式
 *  public interface PayAway {
 *
 *     void printfByType(String type,Object args);
 * }
 *
 * 我们实现以下两种支付方式..
 * 并且需要注意的是 T 目标接口上  所有的策略方法都需要以ByType 结尾,否则仅仅执行的是代理上的目标方法...或者返回null
 *
 * 此api 所有的equals / hashCode / toString 都是代理对象的方法执行,所有其他策略服务的方法均不执行...
 *
 * 于是我们实现以下两种策略服务
 * @Service("payAway")
 * public class PersonStrategyService implements PayAway, StrategyService<PayAway> {
 *     @Override
 *     public String getType() {
 *         return "PERSON";
 *     }
 *
 *     @Override
 *     public void printfByType(String type, Object args) {
 *         System.out.println("person printfByType");
 *     }
 * }
 *
 *
 *@Service
 * public class SchoolStrategyService implements PayAway , StrategyService<PayAway> {
 *     @Override
 *     public String getType() {
 *         return "SCHOOL";
 *     }
 *
 *     @Override
 *     public void printfByType(String type, Object args) {
 *         System.out.printf("%s : printfByType: %s,args: %s%n", getType(),type,args);
 *     }
 * }
 *
 * 这里任意一个@Service 注明将来可能会在程序的任何位置注册的名称 例如它是payAway
 *
 * 我们在另外一个地方注册它 ..
 * class X {
 *       @Autowired
 *     private PayAway payAway;
 *     @Test
 *     public void setPayAwayStrategyService() {
 *         payAway.printfByType("SCHOOL",1);
 *     }
 * }
 *  然后保证payAway 对应即可,那么这里仅仅需要根据指定策略方法即可路由到正确的策略服务上执行...
 *
 *  对此 它打印的结果是:
 * SCHOOL : printfByType: SCHOOL,args: 1
 * 而不是 Person..
 * </pre>
 */
public interface StrategyService<T> {
    /**
     * 策略模式(例如: 1 / 2 ,或者 one / two)
     * 属于第几个策略..
     */
    String getType();

    /**
     * 目标类型信息
     */
    default Class<?> getTargetClass() {
        return this.getClass();
    }
}
