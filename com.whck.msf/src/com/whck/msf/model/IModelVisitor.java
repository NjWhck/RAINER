package com.whck.msf.model;



public interface IModelVisitor {
//	public void visitMovingBox(MovingBox box, Object passAlongArgument);
//	public void visitBook(Book book, Object passAlongArgument);
//	public void visitBoardgame(BoardGame boardgame, Object passAlongArgument);
	public void visitRainer(Rainer rainer, Object passAlongArgument);
	public void visitZone(Zone zone, Object passAlongArgument);
	public void visitDevice(Device device, Object passAlongArgument);
	public void visitVariable(Variable variable, Object passAlongArgument);
}
