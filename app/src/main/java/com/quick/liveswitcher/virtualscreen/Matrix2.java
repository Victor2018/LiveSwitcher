package com.quick.liveswitcher.virtualscreen;

public class Matrix2 {
    double a, b, c, d, tx, ty;

    public static final class MatrixBean{
        double a = 1;
        double b = 0;
        double c = 0;
        double d = 1;
        double tx = 0;
        double ty = 0;

        public double getA() {
            return a;
        }

        public void setA(double a) {
            this.a = a;
        }

        public double getB() {
            return b;
        }

        public void setB(double b) {
            this.b = b;
        }

        public double getC() {
            return c;
        }

        public void setC(double c) {
            this.c = c;
        }

        public double getD() {
            return d;
        }

        public void setD(double d) {
            this.d = d;
        }

        public double getTx() {
            return tx;
        }

        public void setTx(double tx) {
            this.tx = tx;
        }

        public double getTy() {
            return ty;
        }

        public void setTy(double ty) {
            this.ty = ty;
        }
    }

    public void init(double a, double b, double c, double d, double tx, double ty) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.tx = tx;
        this.ty = ty;
    }

    public void initIdentity() {
        this.a = 1;
        this.b = 0;
        this.c = 0;
        this.d = 1;
        this.tx = 0;
        this.ty = 0;
    }

    public void initTranslate(double tx, double ty) {
        this.a = 1;
        this.b = 0;
        this.c = 0;
        this.d = 1;
        this.tx = tx;
        this.ty = ty;
    }

    public void initScale(double sx, double sy) {
        this.a = sx;
        this.b = 0;
        this.c = 0;
        this.d = sy;
        this.tx = 0;
        this.ty = 0;
    }

    public void initRotate(double r) {
        double s = Math.sin(r);
        double c = Math.cos(r);
        this.a = c;
        this.b = s;
        this.c = -s;
        this.d = c;
        this.tx = 0;
        this.ty = 0;
    }

    public void multiply(Matrix2 m1, Matrix2 m2) {
        a = m1.a * m2.a;
        b = 0.0;
        c = 0.0;
        d = m1.d * m2.d;
        tx = m1.tx * m2.a + m2.tx;
        ty = m1.ty * m2.d + m2.ty;

        if (m1.b != 0.0 || m1.c != 0.0 || m2.b != 0.0 || m2.c != 0.0) {
            a += m1.b * m2.c;
            b += m1.a * m2.b + m1.b * m2.d;
            c += m1.c * m2.a + m1.d * m2.c;
            d += m1.c * m2.b;
            tx += m1.ty * m2.c;
            ty += m1.tx * m2.b;
        }
    }

    public void invert() {
        double a, b, c, d, tx, ty;
        double det;

        if ((this.c == 0.0) && (this.b == 0.0)) {
            this.tx = -this.tx;
            this.ty = -this.ty;
            if (this.a != 1.0) {
                if (this.a == 0.0)
                    return;
                this.a = 1.0 / this.a;
                this.tx *= this.a;
            }
            if (this.d != 1.0) {
                if (this.d == 0.0)
                    return;
                this.d = 1.0 / this.d;
                this.ty *= this.d;
            }
        } else {
            det = this.a * this.d - this.b * this.c;
            if (det != 0.0) {
                a = this.a;
                b = this.b;
                c = this.c;
                d = this.d;
                tx = this.tx;
                ty = this.ty;
                this.a = d / det;
                this.b = -b / det;
                this.c = -c / det;
                this.d = a / det;
                this.tx = (c * ty - d * tx) / det;
                this.ty = (b * tx - a * ty) / det;
            }
        }
    }

    public void translate(double tx, double ty) {
        this.tx += this.a * tx + this.c * ty;
        this.ty += this.b * tx + this.d * ty;
    }

    public void scale(double sx, double sy) {
        this.a *= sx;
        this.b *= sx;
        this.c *= sy;
        this.d *= sy;
    }

    public void rotate(double r) {
        double s = Math.sin(r);
        double c = Math.cos(r);
        double ca = c * this.a;
        double cb = c * this.b;
        double cc = c * this.c;
        double cd = c * this.d;
        double sa = s * this.a;
        double sb = s * this.b;
        double sc = s * this.c;
        double sd = s * this.d;

        this.a = ca + sc;
        this.b = cb + sd;
        this.c = cc - sa;
        this.d = cd - sb;
    }

    public void transformDistance(double[] dx, double[] dy) {
        double nx = this.a * dx[0] + this.c * dy[0];
        double ny = this.b * dx[0] + this.d * dy[0];
        dx[0] = nx;
        dy[0] = ny;
    }

    public void transformPoint(double[] x, double[] y) {
        double nx = this.a * x[0] + this.c * y[0] + this.tx;
        double ny = this.b * x[0] + this.d * y[0] + this.ty;
        x[0] = nx;
        y[0] = ny;
    }

    public double[] globalToLocal(double ox, double oy) {
        double id = 1.0 / (this.a * this.d - this.c * this.b);
        double nx = ((ox - this.tx) * this.d + (this.ty - oy) * this.c) * id;
        double ny = ((oy - this.ty) * this.a + (this.tx - ox) * this.b) * id;
        return new double[]{nx,ny};
    }
}

