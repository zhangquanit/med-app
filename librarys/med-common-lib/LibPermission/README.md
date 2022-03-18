### 功能介绍
实现权限动态申请

### 使用方式

```
//由于Activity中的Fragment已经被标记为Deprecated, 官方推荐使用jetpack中的Fragment,所以这里参数使用FragmentActivity实例
val mPermission: PermissionUtil = PermissionUtil(fragmentActivity)

//一次性返回所有请求结构
mPermission.requestPermissions(Manifest.permission.CAMERA,
Manifest.permission.READ_CONTACTS)
.onResult{granted, rejected ->
	//granted 表示已经获得权限的列表,类型List<Permission>
	//rejected 表示被拒绝的权限列表,类型List<Permission>
}

//逐个返回权限申请结果
mPermission.requestPermissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                .onResultForEach { permission ->
			         //permission 权限申请结果,类型Permission
                    
                }

```

在java中调用,回调函数采用lambda表达式, 也可以用Function2和Function1,java中获取权限回调有所不同.
```
//一次性返回所有请求结构
mPermission.requestPermissions(Manifest.permission.CAMERA,
Manifest.permission.READ_CONTACTS)
.onResult((granted, rejected) -> {

           
	//处理结果
           return null;
            
        });

//逐个返回权限申请结果
mPermission.requestPermissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                .onResultForEach(permission -> {

              //处理结果      

              return null;
            
        });
```