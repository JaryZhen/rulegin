
package cn.neucloud.server.common.data.component;

import cn.neucloud.server.common.data.SearchTextBased;
import cn.neucloud.server.common.data.id.ComponentDescriptorId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class DefineComponent extends SearchTextBased<ComponentDescriptorId> {

    private static final long serialVersionUID = 1L;

    @Getter @Setter private ComponentType type;
    @Getter @Setter private ComponentScope scope;
    @Getter @Setter private String dataSourceType;
    @Getter @Setter private String filtersType;
    @Getter @Setter private String actionsType;

    @Getter @Setter private String name;
    @Getter @Setter private String clazz;
    public DefineComponent() {
        super();
    }

    public DefineComponent(ComponentDescriptorId id) {
        super(id);
    }

    public DefineComponent(DefineComponent component) {
        super(component);
        this.type = component.getType();
        this.scope = component.getScope();

        this.dataSourceType  = component.getDataSourceType();
        this.filtersType = component.getFiltersType();
        this.actionsType = component.getActionsType();

        this.name = component.getName();
        this.clazz = component.getClazz();
    }

    @Override
    public String getSearchText() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefineComponent that = (DefineComponent) o;

        if (type != that.type) return false;
        if (scope != that.scope) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (dataSourceType != null ? !dataSourceType.equals(that.dataSourceType) : that.dataSourceType != null) return false;
        if (filtersType != null ? !filtersType.equals(that.filtersType) : that.filtersType != null) return false;
        if (actionsType != null ? !actionsType.equals(that.actionsType) : that.actionsType != null) return false;

        return clazz != null ? clazz.equals(that.clazz) : that.clazz == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        result = 31 * result + (dataSourceType != null ? dataSourceType.hashCode() : 0);
        result = 31 * result + (filtersType != null ? filtersType.hashCode() : 0);
        result = 31 * result + (actionsType != null ? actionsType.hashCode() : 0);

        return result;
    }
}
