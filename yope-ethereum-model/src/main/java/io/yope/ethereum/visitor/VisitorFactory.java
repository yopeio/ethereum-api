package io.yope.ethereum.visitor;

import com.google.common.collect.Maps;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Method;

import java.util.Optional;

/**
 * Contract factory.
 */
public class VisitorFactory {

    public static Method buildMethod(Method.Type type, final Optional<String> name, final Optional<Object[]> args) {
        return Method.builder().type(type).name(name.isPresent() ? name.get() : null).args(args.isPresent() ? args.get() : null).build();
    }

    public static<T> BlockchainVisitor build(final String accountAddress, final String pwd, final String name, final String content, final T model, Method... methods) {
        Account account = Account.builder().address(accountAddress).passphrase(pwd).build();

        BlockchainVisitor visitor = new BlockchainVisitor() {

            @Override
            public String getContent() {
                return content;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void addMethod(Method method) {
                if (getMethods() == null) {
                    setMethods(Maps.newHashMap());
                }
                getMethods().put(method.getType(), method);
            }

        };

        for (Method method : methods) {
            visitor.addMethod(Method.builder().type(method.getType()).args(method.getArgs()).name(method.getName()).build());
        }
        visitor.setAccount(account);
        visitor.setModel(model);
        return  visitor;
    }
}
