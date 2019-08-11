package by.itstep.karnei.hibernate;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateExample {
    private Session session = null;

    private Session createHibernateSession() {
        SessionFactory sf = null;
        ServiceRegistry srvc = null;
        try {
            Configuration conf = new Configuration().configure();
            conf.addAnnotatedClass(User.class);
            conf.addAnnotatedClass(Auto.class);

            srvc = new StandardServiceRegistryBuilder()
                    .applySettings(conf.getProperties()).build();
            sf = conf.buildSessionFactory(srvc);
            session = sf.openSession();
            System.out.println("Создание сессии");
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
        return session;
    }

    private void saveUser() {
        if (session == null)
            return;

        User user1 = new User();
        user1.setAge(30);
        user1.setName("Vasia");
        user1.setSurname("Vasin");

        User user2 = new User();
        user2.setAge(35);
        user2.setName("Vasia2");
        user2.setSurname("Vasin2");


        Transaction trans = session.beginTransaction();
        session.save(user1);
        session.save(user2);

        Auto auto = new Auto();
        auto.setColor("green");
        auto.setBrand("VW");
        auto.setMark("Golf");
        auto.setUser(user1);
        session.saveOrUpdate(auto);

        auto = new Auto();
        auto.setColor("Red");
        auto.setBrand("Skoda");
        auto.setMark("Kadiaq");
        auto.setUser(user2);
        session.saveOrUpdate(auto);

        session.flush();
        trans.commit();

        System.out.println("user1 : " + user1.toString());
        System.out.println("user2 : " + user2.toString());
        System.out.println("auto : " + auto.toString());
    }


    public void refreshUser(int userId, int autoID) {
        if (session == null)
            return;
        User user1 = (User) session.load(User.class, userId);
        Auto auto = (Auto) session.load(Auto.class, autoID);
        if (auto.getMark().equals("Skoda")) {
            auto.setColor("Blue");
            session.saveOrUpdate(auto);
        }
        user1.setAge(12);
        user1.setName("Julia");
        Transaction trans = session.beginTransaction();
        session.saveOrUpdate(user1);

        session.flush();
        trans.commit();

        System.out.println("user1 : " + user1.toString());
        System.out.println("auto : " + auto.toString());
    }

    public void setUserAuto(int userId, int autoId) {
        if (session == null)
            return;
        User user = (User) session.load(User.class, userId);
        Auto auto = (Auto) session.load(Auto.class, autoId);
        auto.setUser(user);

        Transaction trans = session.beginTransaction();
        session.saveOrUpdate(auto);
        session.flush();
        trans.commit();
    }

    public HibernateExample() {
        // Создание сессии
        session = createHibernateSession();
        if (session != null) {
            System.out.println("session is created");
            // Добавление записей в таблицу
            saveUser();
            // Изменение записей в таблице
            refreshUser(1, 3);
            setUserAuto(1, 4);
            // Закрытие сессии
            session.close();
        } else {
            System.out.println("session is not created");
        }
    }

    public static void main(String[] args) {
        new HibernateExample();
        System.exit(0);
    }
}