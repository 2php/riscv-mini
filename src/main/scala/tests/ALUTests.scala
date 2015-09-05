package mini

import Chisel._

class ALUTests(c: ALUTop) extends Tester(c) {
  implicit def toBigInt(x: UInt) = x.litValue()
  for (i <- 0 until 25) {
    val rnd1 = (0x1 << 31) | (rnd.nextInt() & 0x7FFFFFFF)
    val rnd2 = (0xFFFF << 16) | (0x1 << 15) | (rnd.nextInt() & 0x7FFF)
    val A = BigInt(rnd1 >>> 1) << 1 | rnd1 & 1
    val B = BigInt(rnd2 >>> 1) << 1 | rnd2 & 1
    // gold results
    val sum  = A.toInt + B.toInt
    val diff = A.toInt - B.toInt
    val slt  = if (A.toInt < B.toInt) 1 else 0
    val sltu = if (A < B) 1 else 0
    val sll  = A.toInt << (B.toInt & 0x1f)
    val srl  = A.toInt >>> (B.toInt & 0x1f)
    val sra  = A.toInt >> (B.toInt & 0x1f)

    poke(c.io.A, A)
    poke(c.io.B, B)

    println("*** LUI ***")
    poke(c.io.opcode, Opcode.LUI)
    poke(c.io.funct, rnd.nextInt() & 0x7)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, B)

    println("*** AUIPC ***")
    poke(c.io.opcode, Opcode.AUIPC)
    poke(c.io.funct, rnd.nextInt() & 0x7)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** JAL ***")
    poke(c.io.opcode, Opcode.JAL)
    poke(c.io.funct, rnd.nextInt() & 0x7)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** JALR ***")
    poke(c.io.opcode, Opcode.JALR)
    poke(c.io.funct, rnd.nextInt() & 0x7)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)
    
    println("*** BEQ ***")
    poke(c.io.opcode, Opcode.BRANCH)
    poke(c.io.funct, Funct3.BEQ)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** BNE ***")
    poke(c.io.opcode, Opcode.BRANCH)
    poke(c.io.funct, Funct3.BNE)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** BLT ***")
    poke(c.io.opcode, Opcode.BRANCH)
    poke(c.io.funct, Funct3.BLT)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** BGE ***")
    poke(c.io.opcode, Opcode.BRANCH)
    poke(c.io.funct, Funct3.BGE)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** BLTU ***")
    poke(c.io.opcode, Opcode.BRANCH)
    poke(c.io.funct, Funct3.BLTU)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** BGEU ***")
    poke(c.io.opcode, Opcode.BRANCH)
    poke(c.io.funct, Funct3.BGEU)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** LB ***")
    poke(c.io.opcode, Opcode.LOAD)
    poke(c.io.funct, Funct3.LB)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** LH ***")
    poke(c.io.opcode, Opcode.LOAD)
    poke(c.io.funct, Funct3.LH)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** LW ***")
    poke(c.io.opcode, Opcode.LOAD)
    poke(c.io.funct, Funct3.LW)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** LBU ***")
    poke(c.io.opcode, Opcode.LOAD)
    poke(c.io.funct, Funct3.LBU)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** LHU ***")
    poke(c.io.opcode, Opcode.LOAD)
    poke(c.io.funct, Funct3.LHU)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)
 
    println("*** SB ***")
    poke(c.io.opcode, Opcode.STORE)
    poke(c.io.funct, Funct3.SB)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** SH ***")
    poke(c.io.opcode, Opcode.STORE)
    poke(c.io.funct, Funct3.SH)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** SW ***")
    poke(c.io.opcode, Opcode.STORE)
    poke(c.io.funct, Funct3.SW)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sum)

    println("*** ADD ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.ADD)
    poke(c.io.add_rshift_type, AddRshiftType.ADD)
    expect(c.io.out, sum)

    println("*** SUB ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.ADD)
    poke(c.io.add_rshift_type, AddRshiftType.SUB)
    expect(c.io.out, diff)

    println("*** SLL ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.SLL)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sll)
    
    println("*** SLT ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.SLT)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, slt)

    println("*** SLTU ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.SLTU)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sltu)

    println("*** XOR ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.XOR)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, A ^ B)
    
    println("*** SRL ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.SR)
    poke(c.io.add_rshift_type, AddRshiftType.SRL)
    expect(c.io.out, srl)
    
    println("*** SRA ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.SR)
    poke(c.io.add_rshift_type, AddRshiftType.SRA)
    expect(c.io.out, sra)
    
    println("*** OR ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.OR)
    poke(c.io.add_rshift_type, AddRshiftType.SRA)
    expect(c.io.out, A | B)

    println("*** AND ***")
    poke(c.io.opcode, Opcode.RTYPE)
    poke(c.io.funct, Funct3.AND)
    poke(c.io.add_rshift_type, AddRshiftType.SRA)
    expect(c.io.out, A & B)

    println("*** ADDI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.ADD)
    poke(c.io.add_rshift_type, AddRshiftType.ADD)
    expect(c.io.out, sum)

    println("*** SUBI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.ADD)
    poke(c.io.add_rshift_type, AddRshiftType.SUB)
    expect(c.io.out, diff)

    println("*** SLLI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.SLL)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sll)
    
    println("*** SLTI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.SLT)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, slt)

    println("*** SLTIU ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.SLTU)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, sltu)

    println("*** XORI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.XOR)
    poke(c.io.add_rshift_type, rnd.nextInt() & 0x1)
    expect(c.io.out, A ^ B)
    
    println("*** SRLI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.SR)
    poke(c.io.add_rshift_type, AddRshiftType.SRL)
    expect(c.io.out, srl)
    
    println("*** SRAI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.SR)
    poke(c.io.add_rshift_type, AddRshiftType.SRA)
    expect(c.io.out, sra)
    
    println("*** ORI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.OR)
    poke(c.io.add_rshift_type, AddRshiftType.SRA)
    expect(c.io.out, A | B)

    println("*** ANDI ***")
    poke(c.io.opcode, Opcode.ITYPE)
    poke(c.io.funct, Funct3.AND)
    poke(c.io.add_rshift_type, AddRshiftType.SRA)
    expect(c.io.out, A & B)
  }
}
