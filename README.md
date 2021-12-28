# springboot-example
springboot实例

## Swagger
@RequestMapping("/getRoles")不加RequestMethod修饰，swagger3会默认导入所有类型的方法。
并不是因为@ApiModel这个注解让实体显示在这里了，而是只要出现在接口方法的返回值上的实体都会显示在这里，而@ApiModel和@ApiModelProperty这两个注解只是为实体添加注释的。

@ApiModel为类添加注释

@ApiModelProperty为类属性添加注释

常用注解
Swagger的所有注解定义在io.swagger.annotations包下

下面列一些经常用到的，未列举出来的可以另行查阅说明：

Swagger注解	简单说明
@Api(tags = "xxx模块说明")	作用在模块类上
@ApiOperation("xxx接口说明")	作用在接口方法上
@ApiModel("xxxPOJO说明")	作用在模型类上：如VO、BO
@ApiModelProperty(value = "xxx属性说明",hidden = true)	作用在类方法和属性上，hidden设置为true可以隐藏该属性
@ApiParam("xxx参数说明")	作用在参数、方法和字段上，类似@ApiModelProperty