package myminiTests

import aoplib.breakpoint.BreakpointAspect
import aoplib.custom.AnnotatingAspect
import aoplib.histogram.{HistogramAspect, HistogramSignal}
import chisel3._
import chisel3.aop._
import chisel3.aop.injecting.InjectingAspect
import chisel3.experimental.dontTouch
import firrtl.{AnnotationSeq, Transform}
import firrtl.transforms.DontTouchAnnotation
import freechips.rocketchip.config.Parameters
import mini._

// Contains my Aspect Library for testing Mini's ALU
// These functions and objects could exist anywhere
object TileTesterALUAspects {

  // We can reuse functions like this between Aspect instantiations, such as this one
  // This is the primary way to reuse aspect code (via functions, not the aspects themselves)
  def selectALU(tester: TileTester): Seq[ALU] = Seq(tester.dut.asInstanceOf[mini.Tile].core.dpath.alu)

  // We can even assign to values, but this obviously should be done WITH EXTREME CARE
  // Adding this aspect will break EVERYTHING....
  val connected = InjectingAspect(
    selectALU,
    { alu: ALU =>
      alu.io.out := 0.U
    }
  )

  // We can create an aspect that injects additional ALU RTL, such as this logging function
  val inlinedLogging = InjectingAspect(
    selectALU,
    { alu: ALU =>
      when(alu.io.alu_op === 15.U) {
        printf("A == %d, B == %d, opcode == %d\n", alu.io.A, alu.io.B, alu.io.alu_op)
      }
    }
  )

  // We can create an aspect that injects additional ALU annotations
  val annotated = InjectingAspect(
    selectALU,
    { alu: ALU =>
      dontTouch(alu.io.B)
    }
  )

  // The code in an InjectingAspect is just like Chisel - you can even declare and instantiate other modules!
  val instanced = InjectingAspect(
    selectALU,
    { alu: ALU =>
      class Logger(p: Parameters) extends Module {
        val io = IO(new Bundle {
          val A = Input(UInt())
          val B = Input(UInt())
          val alu_op = Input(UInt(4.W))
        })
        when(io.alu_op === 0.U) {
          printf("A == %d, B == %d, opcode == %d\n", io.A, io.B, io.alu_op)
        }
      }

      val logger = Module(new Logger(alu.p))
      logger.io.A := alu.io.A
      logger.io.B := alu.io.B
      logger.io.alu_op := alu.io.alu_op
    }
  )

  // A design can even encode their own aspects, via functions, in the design themselves!
  // In this example, the ALU writer added a handy-dandy function to ALU which generates the
  //   logging RTL themselves. All this aspect needs to do is call the function!
  // In this way, designers can colocate their aspect knowledge, not directly instantiate it, but enable others
  //   to do so easily (without changing the source code!!!)
  val libraried = InjectingAspect(
    selectALU,
    { alu: ALU => alu.logALUOp(15) }
  )

  // Logging isn't the only thing we can do!
  // This aspect records the values of a signal over the course of the execution, and then afterwards, prints out
  //   the value of each bin, cycle by cycle, and then terminates the simulation.
  // We can even histogram multiple signals in the same module!
  // This is an example of the power exposed with aspects. We can create entire libraries of powerful aspects that
  //   are completely design agnostic, like this HistogramAspect
  val aluHistogram = HistogramAspect[TileTester, ALU](
    selectALU,
    { alu: ALU => Seq(new HistogramSignal(alu.io.alu_op)) ++ ALU.histogram(alu) },
    { tester: TileTester => tester.isDone },
    { tester: TileTester => tester.setDone }
  )

  val breakpoint = BreakpointAspect(
    {top: TileTester => top.collectDeep { case c: Cache if c.instanceName == "icache" => c } }, // Instance to add breakpoint
    {cache: Cache =>
      val r = RegNext(cache.state)
      cache.state === r
    }, // Breakpoint Condition
    {cache: Cache => cache.rdata +: (cache.registers().collect { case b: Bits => b } ++ cache.ports()) }, // Signals to record values
    {cache: Cache => (cache.clock, cache.reset) }, // Clock/Reset signals
    "mini/src/main/scala/mini/Cache.scala" // Source file to annotate
  )

  val addAnnotations = AnnotatingAspect {
    top: TileTester => Seq(DontTouchAnnotation(top.dut.io.toTarget))
  }
  class TileSimpleTests extends TileTests(SimpleTests, aspects = Seq(addAnnotations))

}


class TileSimpleTests extends TileTests(SimpleTests, aspects = Nil)

class TileSimpleTestsWithLogger extends TileTests(SimpleTests, aspects = Seq(TileTesterALUAspects.libraried))

class TileSimpleTestsWithHistogrammer extends TileTests(SimpleTests, aspects = Seq(TileTesterALUAspects.aluHistogram))

class TileSimpleTestsWithLoggerAndHistogrammer extends TileTests(SimpleTests, aspects = Seq(TileTesterALUAspects.libraried, TileTesterALUAspects.aluHistogram))

class TileSimpleTestsWithBreakpoint extends TileTests(SimpleTests, aspects = Seq(TileTesterALUAspects.breakpoint))






