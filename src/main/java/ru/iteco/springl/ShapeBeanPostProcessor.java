package ru.iteco.springl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.iteco.springl.components.Shape;

import java.util.HashMap;
import java.util.Map;

@Component
public class ShapeBeanPostProcessor implements BeanPostProcessor {

    private static int count = 0;

    private Map<Object, String> map = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Shape) {
            Shape shape = (Shape) bean;
            String s = String.valueOf(count++);
            shape.setName("beforeInitialized" + beanName + s);
            System.out.println("BeanPostProcessor postProcessBeforeInitialization " + shape.getName());
            map.put(bean, s);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Shape) {
            Shape shape = (Shape) bean;
            String s = map.get(bean);
            shape.setName("afterInitialized" + beanName + s);
            System.out.println("BeanPostProcessor postProcessAfterInitialization " + shape.getName());
        }
        return bean;
    }
}
