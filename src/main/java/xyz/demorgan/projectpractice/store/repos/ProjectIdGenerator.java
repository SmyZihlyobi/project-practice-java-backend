package xyz.demorgan.projectpractice.store.repos;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import xyz.demorgan.projectpractice.store.entity.Project;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProjectIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {

        Project project = (Project) object;
        String sequenceName = project.isStudentProject()
                ? "student_project_seq"
                : "project_seq";

        try (Connection connection = session.getJdbcConnectionAccess().obtainConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(
                    "SELECT nextval('" + sequenceName + "')"
            );

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new HibernateException("Error generating ID", e);
        }
        throw new HibernateException("Unable to generate ID");
    }
}