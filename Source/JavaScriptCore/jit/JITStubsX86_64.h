/*
 * Copyright (C) 2008, 2009, 2013 Apple Inc. All rights reserved.
 * Copyright (C) 2008 Cameron Zwarich <cwzwarich@uwaterloo.ca>
 * Copyright (C) Research In Motion Limited 2010, 2011. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of Apple Computer, Inc. ("Apple") nor the names of
 *     its contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY APPLE AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL APPLE OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#ifndef JITStubsX86_64_h
#define JITStubsX86_64_h

#include "JITStubsX86Common.h"

#if !CPU(X86_64)
#error "JITStubsX86_64.h should only be #included if CPU(X86_64)"
#endif

#if !USE(JSVALUE64)
#error "JITStubsX86_64.h only implements USE(JSVALUE64)"
#endif

namespace JSC {

#if COMPILER(GCC)

// These ASSERTs remind you that, if you change the layout of JITStackFrame, you
// need to change the assembly trampolines below to match.
COMPILE_ASSERT(offsetof(struct JITStackFrame, callFrame) == 0x58, JITStackFrame_callFrame_offset_matches_ctiTrampoline);
COMPILE_ASSERT(offsetof(struct JITStackFrame, code) == 0x48, JITStackFrame_code_offset_matches_ctiTrampoline);
COMPILE_ASSERT(offsetof(struct JITStackFrame, savedRBX) == 0x78, JITStackFrame_stub_argument_space_matches_ctiTrampoline);

asm (
".text\n"
".globl " SYMBOL_STRING(ctiTrampoline) "\n"
HIDE_SYMBOL(ctiTrampoline) "\n"
SYMBOL_STRING(ctiTrampoline) ":" "\n"
    "pushq %rbp" "\n"
    "movq %rsp, %rbp" "\n"
    "pushq %r12" "\n"
    "pushq %r13" "\n"
    "pushq %r14" "\n"
    "pushq %r15" "\n"
    "pushq %rbx" "\n"
    // Form the JIT stubs area
    "pushq %r9" "\n"
    "pushq %r8" "\n"
    "pushq %rcx" "\n"
    "pushq %rdx" "\n"
    "pushq %rsi" "\n"
    "pushq %rdi" "\n"
    "subq $0x48, %rsp" "\n"
    "movq $512, %r12" "\n"
    "movq $0xFFFF000000000000, %r14" "\n"
    "movq $0xFFFF000000000002, %r15" "\n"
    "movq %rdx, %r13" "\n"
    "call *%rdi" "\n"
    "addq $0x78, %rsp" "\n"
    "popq %rbx" "\n"
    "popq %r15" "\n"
    "popq %r14" "\n"
    "popq %r13" "\n"
    "popq %r12" "\n"
    "popq %rbp" "\n"
    "ret" "\n"
".globl " SYMBOL_STRING(ctiTrampolineEnd) "\n"
HIDE_SYMBOL(ctiTrampolineEnd) "\n"
SYMBOL_STRING(ctiTrampolineEnd) ":" "\n"
);

asm (
".globl " SYMBOL_STRING(ctiVMThrowTrampoline) "\n"
HIDE_SYMBOL(ctiVMThrowTrampoline) "\n"
SYMBOL_STRING(ctiVMThrowTrampoline) ":" "\n"
    "movq %rsp, %rdi" "\n"
    "call " LOCAL_REFERENCE(cti_vm_throw) "\n"
    "int3" "\n"
);

asm (
".globl " SYMBOL_STRING(ctiVMThrowTrampolineSlowpath) "\n"
HIDE_SYMBOL(ctiVMThrowTrampolineSlowpath) "\n"
SYMBOL_STRING(ctiVMThrowTrampolineSlowpath) ":" "\n"
    "movq %r13, %rdi" "\n"
    "call " LOCAL_REFERENCE(cti_vm_throw_slowpath) "\n"
    // When cti_vm_throw_slowpath returns, rax has callFrame and rdx has handler address
    "jmp *%rdx" "\n"
);

asm (
".globl " SYMBOL_STRING(ctiOpThrowNotCaught) "\n"
HIDE_SYMBOL(ctiOpThrowNotCaught) "\n"
SYMBOL_STRING(ctiOpThrowNotCaught) ":" "\n"
    "addq $0x78, %rsp" "\n"
    "popq %rbx" "\n"
    "popq %r15" "\n"
    "popq %r14" "\n"
    "popq %r13" "\n"
    "popq %r12" "\n"
    "popq %rbp" "\n"
    "ret" "\n"
);

#if USE(MASM_PROBE)
asm (
".globl " SYMBOL_STRING(ctiMasmProbeTrampoline) "\n"
HIDE_SYMBOL(ctiMasmProbeTrampoline) "\n"
SYMBOL_STRING(ctiMasmProbeTrampoline) ":" "\n"

    "pushfq" "\n"

    // MacroAssembler::probe() has already generated code to store some values.
    // Together with the rflags pushed above, the top of stack now looks like
    // this:
    //     esp[0 * ptrSize]: rflags
    //     esp[1 * ptrSize]: return address / saved rip
    //     esp[2 * ptrSize]: probeFunction
    //     esp[3 * ptrSize]: arg1
    //     esp[4 * ptrSize]: arg2
    //     esp[5 * ptrSize]: saved rax
    //     esp[6 * ptrSize]: saved rsp

    "movq %rsp, %rax" "\n"
    "subq $" STRINGIZE_VALUE_OF(PROBE_SIZE) ", %rsp" "\n"

    // The X86_64 ABI specifies that the worse case stack alignment requirement
    // is 32 bytes.
    "andq $~0x1f, %rsp" "\n"

    "movq %rbp, " STRINGIZE_VALUE_OF(PROBE_CPU_EBP_OFFSET) "(%rsp)" "\n"
    "movq %rsp, %rbp" "\n" // Save the ProbeContext*.

    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_ECX_OFFSET) "(%rbp)" "\n"
    "movq %rdx, " STRINGIZE_VALUE_OF(PROBE_CPU_EDX_OFFSET) "(%rbp)" "\n"
    "movq %rbx, " STRINGIZE_VALUE_OF(PROBE_CPU_EBX_OFFSET) "(%rbp)" "\n"
    "movq %rsi, " STRINGIZE_VALUE_OF(PROBE_CPU_ESI_OFFSET) "(%rbp)" "\n"
    "movq %rdi, " STRINGIZE_VALUE_OF(PROBE_CPU_EDI_OFFSET) "(%rbp)" "\n"

    "movq 0 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_EFLAGS_OFFSET) "(%rbp)" "\n"
    "movq 1 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_EIP_OFFSET) "(%rbp)" "\n"
    "movq 2 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_PROBE_FUNCTION_OFFSET) "(%rbp)" "\n"
    "movq 3 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_ARG1_OFFSET) "(%rbp)" "\n"
    "movq 4 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_ARG2_OFFSET) "(%rbp)" "\n"
    "movq 5 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_EAX_OFFSET) "(%rbp)" "\n"
    "movq 6 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_ESP_OFFSET) "(%rbp)" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_JIT_STACK_FRAME_OFFSET) "(%rbp)" "\n"

    "movq %r8, " STRINGIZE_VALUE_OF(PROBE_CPU_R8_OFFSET) "(%rbp)" "\n"
    "movq %r9, " STRINGIZE_VALUE_OF(PROBE_CPU_R9_OFFSET) "(%rbp)" "\n"
    "movq %r10, " STRINGIZE_VALUE_OF(PROBE_CPU_R10_OFFSET) "(%rbp)" "\n"
    "movq %r11, " STRINGIZE_VALUE_OF(PROBE_CPU_R11_OFFSET) "(%rbp)" "\n"
    "movq %r12, " STRINGIZE_VALUE_OF(PROBE_CPU_R12_OFFSET) "(%rbp)" "\n"
    "movq %r13, " STRINGIZE_VALUE_OF(PROBE_CPU_R13_OFFSET) "(%rbp)" "\n"
    "movq %r14, " STRINGIZE_VALUE_OF(PROBE_CPU_R14_OFFSET) "(%rbp)" "\n"
    "movq %r15, " STRINGIZE_VALUE_OF(PROBE_CPU_R15_OFFSET) "(%rbp)" "\n"

    "movdqa %xmm0, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM0_OFFSET) "(%rbp)" "\n"
    "movdqa %xmm1, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM1_OFFSET) "(%rbp)" "\n"
    "movdqa %xmm2, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM2_OFFSET) "(%rbp)" "\n"
    "movdqa %xmm3, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM3_OFFSET) "(%rbp)" "\n"
    "movdqa %xmm4, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM4_OFFSET) "(%rbp)" "\n"
    "movdqa %xmm5, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM5_OFFSET) "(%rbp)" "\n"
    "movdqa %xmm6, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM6_OFFSET) "(%rbp)" "\n"
    "movdqa %xmm7, " STRINGIZE_VALUE_OF(PROBE_CPU_XMM7_OFFSET) "(%rbp)" "\n"

    "movq %rbp, %rdi" "\n" // the ProbeContext* arg.
    "call *" STRINGIZE_VALUE_OF(PROBE_PROBE_FUNCTION_OFFSET) "(%rbp)" "\n"

    // To enable probes to modify register state, we copy all registers
    // out of the ProbeContext before returning.

    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EDX_OFFSET) "(%rbp), %rdx" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EBX_OFFSET) "(%rbp), %rbx" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_ESI_OFFSET) "(%rbp), %rsi" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EDI_OFFSET) "(%rbp), %rdi" "\n"

    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R8_OFFSET) "(%rbp), %r8" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R9_OFFSET) "(%rbp), %r9" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R10_OFFSET) "(%rbp), %r10" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R11_OFFSET) "(%rbp), %r11" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R12_OFFSET) "(%rbp), %r12" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R13_OFFSET) "(%rbp), %r13" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R14_OFFSET) "(%rbp), %r14" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_R15_OFFSET) "(%rbp), %r15" "\n"

    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM0_OFFSET) "(%rbp), %xmm0" "\n"
    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM1_OFFSET) "(%rbp), %xmm1" "\n"
    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM2_OFFSET) "(%rbp), %xmm2" "\n"
    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM3_OFFSET) "(%rbp), %xmm3" "\n"
    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM4_OFFSET) "(%rbp), %xmm4" "\n"
    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM5_OFFSET) "(%rbp), %xmm5" "\n"
    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM6_OFFSET) "(%rbp), %xmm6" "\n"
    "movdqa " STRINGIZE_VALUE_OF(PROBE_CPU_XMM7_OFFSET) "(%rbp), %xmm7" "\n"

    // There are 6 more registers left to restore:
    //     rax, rcx, rbp, rsp, rip, and rflags.
    // We need to handle these last few restores carefully because:
    //
    // 1. We need to push the return address on the stack for ret to use
    //    That means we need to write to the stack.
    // 2. The user probe function may have altered the restore value of esp to
    //    point to the vicinity of one of the restore values for the remaining
    //    registers left to be restored.
    //    That means, for requirement 1, we may end up writing over some of the
    //    restore values. We can check for this, and first copy the restore
    //    values to a "safe area" on the stack before commencing with the action
    //    for requirement 1.
    // 3. For both requirement 2, we need to ensure that the "safe area" is
    //    protected from interrupt handlers overwriting it. Hence, the esp needs
    //    to be adjusted to include the "safe area" before we start copying the
    //    the restore values.

    "movq %rbp, %rax" "\n"
    "addq $" STRINGIZE_VALUE_OF(PROBE_CPU_EFLAGS_OFFSET) ", %rax" "\n"
    "cmpq %rax, " STRINGIZE_VALUE_OF(PROBE_CPU_ESP_OFFSET) "(%rbp)" "\n"
    "jg " SYMBOL_STRING(ctiMasmProbeTrampolineEnd) "\n"

    // Locate the "safe area" at 2x sizeof(ProbeContext) below where the new
    // rsp will be. This time we don't have to 32-byte align it because we're
    // not using to store any xmm regs.
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_ESP_OFFSET) "(%rbp), %rax" "\n"
    "subq $2 * " STRINGIZE_VALUE_OF(PROBE_SIZE) ", %rax" "\n"
    "movq %rax, %rsp" "\n"

    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EAX_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_EAX_OFFSET) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_ECX_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_ECX_OFFSET) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EBP_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_EBP_OFFSET) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_ESP_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_ESP_OFFSET) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EIP_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_EIP_OFFSET) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EFLAGS_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, " STRINGIZE_VALUE_OF(PROBE_CPU_EFLAGS_OFFSET) "(%rax)" "\n"
    "movq %rax, %rbp" "\n"

SYMBOL_STRING(ctiMasmProbeTrampolineEnd) ":" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_ESP_OFFSET) "(%rbp), %rax" "\n"
    "subq $5 * " STRINGIZE_VALUE_OF(PTR_SIZE) ", %rax" "\n"
    // At this point, %rsp should be < %rax.

    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EFLAGS_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, 0 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EAX_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, 1 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_ECX_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, 2 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EBP_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, 3 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax)" "\n"
    "movq " STRINGIZE_VALUE_OF(PROBE_CPU_EIP_OFFSET) "(%rbp), %rcx" "\n"
    "movq %rcx, 4 * " STRINGIZE_VALUE_OF(PTR_SIZE) "(%rax)" "\n"
    "movq %rax, %rsp" "\n"

    "popfq" "\n"
    "popq %rax" "\n"
    "popq %rcx" "\n"
    "popq %rbp" "\n"
    "ret" "\n"
);
#endif // USE(MASM_PROBE)

#endif // COMPILER(GCC)

#if COMPILER(MSVC)

// These ASSERTs remind you that, if you change the layout of JITStackFrame, you
// need to change the assembly trampolines in JITStubsMSVC64.asm to match.
COMPILE_ASSERT(offsetof(struct JITStackFrame, code) % 16 == 0x0, JITStackFrame_maintains_16byte_stack_alignment);
COMPILE_ASSERT(offsetof(struct JITStackFrame, savedRBX) == 0x58, JITStackFrame_stub_argument_space_matches_ctiTrampoline);

#endif // COMPILER(MSVC)

} // namespace JSC

#endif // JITStubsX86_64_h