//  Epsilon.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package br.otimizes.oplatool.core.jmetal4.qualityIndicator;

import br.otimizes.oplatool.core.jmetal4.qualityIndicator.util.MetricsUtil;

/**
 * This class implements the unary epsilon additive indicator as proposed in
 * E. Zitzler, E. Thiele, L. Laummanns, M., Fonseca, C., and Grunert da Fonseca.
 * V (2003): Performance Assesment of Multiobjective Optimizers: An Analysis and
 * Review. The code is the a Java version of the orginal metric implementation
 * by Eckart Zitzler.
 * It can be used also as a command line program just by typing
 * $java br.otimizes.oplatool.core.jmetal4.qualityIndicator.Epsilon <solutionFrontFile> <trueFrontFile> <numberOfOjbectives>
 */

public class Epsilon {

    int dim_;
    int[] obj_;
    int method_;
    public MetricsUtil utils_ = new MetricsUtil();

    /**
     * Returns the epsilon indicator.
     *
     * @param b True Pareto front
     * @param a Solution front
     * @return the value of the epsilon indicator
     */
    public double epsilon(double[][] b, double[][] a, int dim) {
        int i, j, k;
        double eps, eps_j = 0.0, eps_k = 0.0, eps_temp;

        dim_ = dim;
        set_params();

        if (method_ == 0)
            eps = Double.MIN_VALUE;
        else
            eps = 0;

        for (i = 0; i < a.length; i++) {
            for (j = 0; j < b.length; j++) {
                for (k = 0; k < dim_; k++) {
                    switch (method_) {
                        case 0:
                            if (obj_[k] == 0)
                                eps_temp = b[j][k] - a[i][k];
                            else
                                eps_temp = a[i][k] - b[j][k];
                            break;
                        default:
                            if ((a[i][k] < 0 && b[j][k] > 0) ||
                                    (a[i][k] > 0 && b[j][k] < 0) ||
                                    (a[i][k] == 0 || b[j][k] == 0)) {
                                System.err.println("error in data file");
                                System.exit(0);
                            }
                            if (obj_[k] == 0)
                                eps_temp = b[j][k] / a[i][k];
                            else
                                eps_temp = a[i][k] / b[j][k];
                            break;
                    }
                    if (k == 0)
                        eps_k = eps_temp;
                    else if (eps_k < eps_temp)
                        eps_k = eps_temp;
                }
                if (j == 0)
                    eps_j = eps_k;
                else if (eps_j > eps_k)
                    eps_j = eps_k;
            }
            if (i == 0)
                eps = eps_j;
            else if (eps < eps_j)
                eps = eps_j;
        }
        return eps;
    } // epsilon

    /**
     * Established the params by default
     */
    void set_params() {
        int i;
        obj_ = new int[dim_];
        for (i = 0; i < dim_; i++) {
            obj_[i] = 0;
        }
        method_ = 0;
    } // set_params
} // Epsilon
