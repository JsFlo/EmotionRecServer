package com.emotionrec.tfinference.exts

import org.tensorflow.*


// Add Constant
fun Graph.addConstant(name: String, value: Any): Output<*> {
    return createTensor(value).use { tensor ->
        this.addTensorConstant(tensor, name)
    }
}

fun Graph.addTensorConstant(value: Tensor<*>, name: String): Output<*> {
    return opBuilder("Const", name)
            .setAttr("dtype", value.dataType())
            .setAttr("value", value)
            .build().output<DataType>(0)
}

// Add Placeholder
fun Graph.addPlaceholder(name: String, dataType: DataType): Output<*> {
    return opBuilder("Placeholder", name)
            .setAttr("dtype", dataType)
            .build().output<DataType>(0)
}

fun Graph.addVariableAndAssignValue(name: String, dataType: DataType, shape: Shape, value: Output<*>): Output<*> {
    return assignValue(value, addVariable(name, dataType, shape))
}

fun Graph.addVariable(name: String, dataType: DataType, shape: Shape): Output<*> {
    return opBuilder("Variable", name)
            .setAttr("dtype", dataType)
            .setAttr("shape", shape)
            .build().output<DataType>(0)
}

fun Graph.assignValue(value: Output<*>, variable: Output<*>): Output<*> {
    return opBuilder("Assign", "Assign/" + variable.op().name())
            .addInput(variable)
            .addInput(value)
            .build().output<DataType>(0)
}

fun Graph.assignValue(name: String, value: Any, variable: Output<*>): Output<*> {
    return assignValue(addConstant(name, value), variable)
}


// Operations

fun Graph.Operation(name: String, operation: OperationType, vararg outputs: Output<*>): Output<*> {
    return Operation(name, operation, outputs.toList())
}

fun Graph.Operation(name: String, operation: OperationType, outputs: List<Output<*>>): Output<*> {
    if (!outputs.isEmpty()) {
        val firstOutput = outputs.get(0)
        val subList = outputs.toMutableList()
        subList.removeAt(0)
        return subList.foldIndexed(firstOutput, { index, output, nextOutput ->
            Operation(name + index, operation, output, nextOutput)
        })
    } else {
        throw IllegalArgumentException("Outputs is empty")
    }
}

fun Graph.Operation(name: String, operation: OperationType, value1: Output<*>, value2: Output<*>): Output<*> {

    println("""
    Operation name: $name
        op: ${operation.opName}
        val1: ${value1.op().name()}
        val2: ${value2.op().name()}
    """)

    return opBuilder(operation.opName, name)
            .addInput(value1)
            .addInput(value2)
            .build().output<OperationType>(0)

}

enum class OperationType(val opName: String) {
    ADD("Add"),
    MUL("Mul"),
    DIV("Div"),
    SUB("Sub");
}
