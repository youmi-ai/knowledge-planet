# zx_object_get_property

## NAME

<!-- Updated by update-docs-from-fidl, do not edit. -->

Ask for various properties of various kernel objects.

## SYNOPSIS

<!-- Updated by update-docs-from-fidl, do not edit. -->

```c
#include <zircon/syscalls.h>

zx_status_t zx_object_get_property(zx_handle_t handle,
                                   uint32_t property,
                                   void* value,
                                   size_t value_size);
```

## DESCRIPTION

`zx_object_get_property()` requests the value of a kernel object's property.
Getting a property requires **ZX_RIGHT_GET_PROPERTY** rights on the handle.

The *handle* parameter indicates the target kernel object. Different properties
only work on certain types of kernel objects, as described below.

The *property* parameter indicates which property to get/set. Property values
have the prefix **ZX_PROP_**, and are described below.

The *value* parameter holds the property value, and must be a pointer to a
buffer of *value_size* bytes. Different properties expect different value
types/sizes as described below.

## PROPERTIES

Property values have the prefix **ZX_PROP_**, and are defined in

```
#include <zircon/syscalls/object.h>
```

### ZX_PROP_NAME

*handle* type: **(Most types)**

*value* type: `char[ZX_MAX_NAME_LEN]`

Allowed operations: **get**, **set**

The name of the object, as a NUL-terminated string.

### ZX_PROP_REGISTER_FS and ZX_PROP_REGISTER_GS

*handle* type: **Thread**

*value* type: `uintptr_t`

Allowed operations: **set**

The value of the x86 FS or GS segment register. `value` must be a
canonical address, and must be a userspace address.

Only defined for x86-64.

### ZX_PROP_PROCESS_DEBUG_ADDR

*handle* type: **Process**

*value* type: `uintptr_t`

Allowed operations: **get**, **set**

The value of ld.so's `_dl_debug_addr`. This can be used by debuggers to
interrogate the state of the dynamic loader.

If this value is set to `ZX_PROCESS_DEBUG_ADDR_BREAK_ON_SET` on process
creation, the loader will manually issue a debug breakpoint when the property
has been set to its correct value. This gives an opportunity to read or modify
the initial state of the program.

### ZX_PROP_PROCESS_BREAK_ON_LOAD

*handle* type: **Process**

*value* type: `uintptr_t`

Allowed operations: **get**, **set**

Determines whether the dynamic loader will issue a debug trap on every load of a
shared library. If set before the first thread of a process runs, it will also
trigger a debug trap for the initial load.

The dynamic loader sets the expected value of `ZX_PROP_PROCESS_DEBUG_ADDR` before
triggering this debug trap. Exception handlers can use this property to query the
dynamic loader's state.

When the dynamic loader issues the debug trap, it sets the value of the `r_brk_on_load`
member on the `r_debug` struct exposed by the dynamic loader. The address of this
struct can be obtained by the `ZX_PROP_PROCESS_DEBUG_ADDR` property.

Any non-zero value is considered to activate this feature. Setting this property to
zero will disable it.

Note: Depending on the architecture, the address reported by the exception might be
different that the one reported by this property. For example, an x64 platform reports
the instruction pointer *after* it executes the instruction.  This means that an x64
platform reports an instruction pointer one byte higher than this property.

### ZX_PROP_PROCESS_VDSO_BASE_ADDRESS

*handle* type: **Process**

*value* type: `uintptr_t`

Allowed operations: **get**

The base address of the vDSO mapping, or zero.

### ZX_PROP_PROCESS_HW_TRACE_CONTEXT_ID

*handle* type: **Process**

*value* type: `uintptr_t`

Allowed operations: **get**

The context ID distinguishes different processes in hardware instruction tracing.
On Intel X86-64 this is the value of register CR3.

To obtain `ZX_PROP_PROCESS_HW_TRACE_CONTEXT_ID`, you must specify
`kernel.enable-debugging-syscalls=true` on the kernel command line. Otherwise,
the function returns **ZX_ERR_NOT_SUPPORTED**.

Currently only defined for X86.

### ZX_PROP_SOCKET_RX_THRESHOLD

*handle* type: **Socket**

*value* type: `size_t`

Allowed operations: **get**, **set**

The size of the read threshold of a socket, in bytes. Setting this will
assert ZX_SOCKET_READ_THRESHOLD if the amount of data that can be read
is greater than or equal to the threshold. Setting this property to zero
will result in the deasserting of ZX_SOCKET_READ_THRESHOLD.

### ZX_PROP_SOCKET_TX_THRESHOLD

*handle* type: **Socket**

*value* type: `size_t`

Allowed operations: **get**, **set**

The size of the write threshold of a socket, in bytes. Setting this will
assert ZX_SOCKET_WRITE_THRESHOLD if the amount of space available for writing
is greater than or equal to the threshold. Setting this property to zero
will result in the deasserting of ZX_SOCKET_WRITE_THRESHOLD. Setting the
write threshold after the peer has closed is an error, and results in a
ZX_ERR_PEER_CLOSED error being returned.

### ZX_PROP_JOB_KILL_ON_OOM

*handle* type: **Job**

*value* type: `size_t`

Allowed operations: **set**

The value of 1 means the Job and its children will be terminated if the
system finds itself in a system-wide low memory situation. Called with 0
(which is the default) opts out the job from being terminated in this
scenario.

### ZX_PROP_EXCEPTION_STATE

*handle* type: **Exception**

*value* type: `uint32_t`

Allowed operations: **get**, **set**

When set to `ZX_EXCEPTION_STATE_HANDLED`, closing the exception handle will
finish exception processing and resume the underlying thread.
`ZX_EXCEPTION_STATE_TRY_NEXT` will instead continue exception processing by
trying the next handler in order.

### ZX_PROP_EXCEPTION_STRATEGY

*handle* type: **Exception**

*value* type: `uint32_t`

Allowed operations: **get**, **set**

If `ZX_EXCEPTION_STRATEGY_SECOND_CHANCE` is set, then the debugger gets a 'second
chance' at handling the exception if the process-level handler fails to do so.

This property can only be set when the handle corresponds to a debugger process
exception channel. Attempting to set this property when the exception channel
is any other type will result in ZX_ERR_BAD_STATE.

## RIGHTS

<!-- Updated by update-docs-from-fidl, do not edit. -->

*handle* must have **ZX_RIGHT_GET_PROPERTY**.

If *property* is **ZX_PROP_PROCESS_DEBUG_ADDR**, *handle* must be of type **ZX_OBJ_TYPE_PROCESS**.

If *property* is **ZX_PROP_PROCESS_BREAK_ON_LOAD**, *handle* must be of type **ZX_OBJ_TYPE_PROCESS**.

If *property* is **ZX_PROP_PROCESS_VDSO_BASE_ADDRESS**, *handle* must be of type **ZX_OBJ_TYPE_PROCESS**.

If *property* is **ZX_PROP_SOCKET_RX_THRESHOLD**, *handle* must be of type **ZX_OBJ_TYPE_SOCKET**.

If *property* is **ZX_PROP_SOCKET_TX_THRESHOLD**, *handle* must be of type **ZX_OBJ_TYPE_SOCKET**.

## RETURN VALUE

`zx_object_get_property()` returns **ZX_OK** on success. In the event of
failure, a negative error value is returned.

## ERRORS

**ZX_ERR_BAD_HANDLE**: *handle* is not a valid handle

**ZX_ERR_WRONG_TYPE**: *handle* is not an appropriate type for *property*

**ZX_ERR_ACCESS_DENIED**: *handle* does not have the necessary rights for the
operation

**ZX_ERR_INVALID_ARGS**: *value* is an invalid pointer

**ZX_ERR_NO_MEMORY**  Failure due to lack of memory.
There is no good way for userspace to handle this (unlikely) error.
In a future build this error will no longer occur.

**ZX_ERR_BUFFER_TOO_SMALL**: *value_size* is too small for *property*

**ZX_ERR_NOT_SUPPORTED**: *property* does not exist

## SEE ALSO

 - [`zx_object_set_property()`]

<!-- References updated by update-docs-from-fidl, do not edit. -->

[`zx_object_set_property()`]: object_set_property.md
