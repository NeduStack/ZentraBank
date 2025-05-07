package fizy.web.app.service.operation;

import fizy.web.app.entity.User;

public interface AccountOperation<T, R> {
    R execute(T request, User user) throws Exception;
}