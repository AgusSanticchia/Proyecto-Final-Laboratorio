package ar.edu.utn.frbb.tup.persistence.entity;

public class BaseEntity {
    private Long Id;

    public BaseEntity(long id) {
        Id = id;
    }


    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }
}
