package com.company.sakila.db0.sakila.payment.generated;

import com.company.sakila.db0.sakila.payment.Payment;
import com.company.sakila.db0.sakila.payment.PaymentImpl;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.common.injector.annotation.ExecuteBefore;
import com.speedment.common.injector.annotation.WithState;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.component.sql.SqlPersistenceComponent;
import com.speedment.runtime.core.component.sql.SqlStreamSupplierComponent;
import com.speedment.runtime.core.exception.SpeedmentException;
import java.sql.ResultSet;
import java.sql.SQLException;
import static com.speedment.common.injector.State.RESOLVED;
import static com.speedment.runtime.core.internal.util.sql.ResultSetUtil.*;

/**
 * The generated Sql Adapter for a {@link
 * com.company.sakila.db0.sakila.payment.Payment} entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedPaymentSqlAdapter {
    
    private final TableIdentifier<Payment> tableIdentifier;
    
    protected GeneratedPaymentSqlAdapter() {
        this.tableIdentifier = TableIdentifier.of("db0", "sakila", "payment");
    }
    
    @ExecuteBefore(RESOLVED)
    void installMethodName(@WithState(RESOLVED) SqlStreamSupplierComponent streamSupplierComponent,
            @WithState(RESOLVED) SqlPersistenceComponent persistenceComponent) {
        streamSupplierComponent.install(tableIdentifier, this::apply);
        persistenceComponent.install(tableIdentifier);
    }
    
    protected Payment apply(ResultSet resultSet) throws SpeedmentException {
        final Payment entity = createEntity();
        try {
            entity.setPaymentId(   resultSet.getInt(1)        );
            entity.setCustomerId(  resultSet.getInt(2)        );
            entity.setStaffId(     resultSet.getShort(3)      );
            entity.setRentalId(    getInt(resultSet, 4)       );
            entity.setAmount(      resultSet.getBigDecimal(5) );
            entity.setPaymentDate( resultSet.getTimestamp(6)  );
            entity.setLastUpdate(  resultSet.getTimestamp(7)  );
        } catch (final SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return entity;
    }
    
    protected PaymentImpl createEntity() {
        return new PaymentImpl();
    }
}