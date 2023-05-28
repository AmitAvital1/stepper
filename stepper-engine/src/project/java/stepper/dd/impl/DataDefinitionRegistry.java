package project.java.stepper.dd.impl;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.dd.impl.enumerator.ZipEnum;
import project.java.stepper.dd.impl.enumerator.ZipEnumeratorDefinition;
import project.java.stepper.dd.impl.file.FileDataDefinition;
import project.java.stepper.dd.impl.list.ListDataDefinition;
import project.java.stepper.dd.impl.mapping.MappingDataDefinition;
import project.java.stepper.dd.impl.number.DoubleDataDefinition;
import project.java.stepper.dd.impl.number.IntegerDataDefinition;
import project.java.stepper.dd.impl.relation.RelationDataDefinition;
import project.java.stepper.dd.impl.string.StringDataDefinition;
import project.java.stepper.exceptions.InvalidUserDataTypeInput;
import project.java.stepper.exceptions.StepperExeption;

import java.util.Arrays;

public enum DataDefinitionRegistry implements DataDefinition{
    STRING(new StringDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    INTEGER(new IntegerDataDefinition()),
    RELATION(new RelationDataDefinition()),
    LIST(new ListDataDefinition()),
    FILE(new FileDataDefinition()),
    MAPPING(new MappingDataDefinition()),
    ZIPENUMERATOR(new ZipEnumeratorDefinition())
    ;

    DataDefinitionRegistry(DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    private final DataDefinition dataDefinition;

    @Override
    public String getName() {
        return dataDefinition.getName();
    }

    @Override
    public boolean isUserFriendly() {
        return dataDefinition.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return dataDefinition.getType();
    }
    public <T> T convertUserInputToDataType(String input, Class<T> expectedDataType) throws StepperExeption {
        try {
            if (this == STRING) {
                return expectedDataType.cast(input);
            }
            else if (this == INTEGER) {
                Integer num = Integer.parseInt(input);
                if(num < 0)
                    throw new InvalidUserDataTypeInput("This input have to be non negative number");
                return expectedDataType.cast(num);
            } else if (this == DOUBLE) {
                Double num = Double.parseDouble(input);
                return expectedDataType.cast(num);
            }else if (this == ZIPENUMERATOR){
                if(!Arrays.stream(ZipEnum.values()).anyMatch(e -> e.toString().equals(input))){
                    throw new InvalidUserDataTypeInput("This input have to be one of <ZIP> or <UNZIP>");
                }
                else if(ZipEnum.ZIP.toString().equals(input))
                    return expectedDataType.cast(ZipEnum.ZIP);
                else
                    return expectedDataType.cast(ZipEnum.UNZIP);
            }
        }catch (NumberFormatException e) {
            throw new InvalidUserDataTypeInput("This input have to be a " + expectedDataType.getSimpleName());
        }
        return null;
    }

}
