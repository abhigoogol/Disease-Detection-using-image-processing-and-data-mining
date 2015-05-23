%	Copyright (c) 2008, Avinash Uppuluri
%	All rights reserved.

%	Redistribution and use in source and binary forms, with or without
%	modification, are permitted provided that the following conditions are met:

%    * Redistributions of source code must retain the above copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in
%      the documentation and/or other materials provided with the distribution

%	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
%	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
%	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
%	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
%	LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
%	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
%	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
%	INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
%	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
%	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
%	POSSIBILITY OF SUCH DAMAGE.


function [out] = GLCM_Features4(glcmin,pairs)
% 
% 
% This is an update of GLCM_Features2 (vectorized) without ismember()
%
% GLCM_Features2 helps to calculate the features from the different GLCMs
% that are input to the function. The GLCMs are stored in a i x j x n
% matrix, where n is the number of GLCMs calculated usually due to the
% different orientation and displacements used in the algorithm. Usually
% the values i and j are equal to 'NumLevels' parameter of the GLCM
% computing function graycomatrix(). Note that matlab quantization values
% belong to the set {1,..., NumLevels} and not from {0,...,(NumLevels-1)}
% as provided in some references
% http://www.mathworks.com/access/helpdesk/help/toolbox/images/graycomatrix
% .html
% 
% This vectorized version of GLCM_FEatures1.m reduces the 19 'for' loops
% used in the earlier code to 5 'for' loops
% http://blogs.mathworks.com/loren/2006/07/12/what-are-you-really-measuring
% /
% Using tic toc and cputime as in above discussion
%
% Although there is a function graycoprops() in Matlab Image Processing
% Toolbox that computes four parameters Contrast, Correlation, Energy,
% and Homogeneity. The paper by Haralick suggests a few more parameters
% that are also computed here. The code is not fully vectorized and hence
% is not an efficient implementation but it is easy to add new features
% based on the GLCM using this code. Takes care of 3 dimensional glcms
% (multiple glcms in a single 3D array)
% 
% If you find that the values obtained are different from what you expect 
% or if you think there is a different formula that needs to be used 
% from the ones used in this code please let me know. 
% A few questions which I have are listed in the link 
% http://www.mathworks.com/matlabcentral/newsreader/view_thread/239608
%
%
%
% Features computed 
% Step:										(out.step)
% Contrast: 			                    (out.contr)
% Entropy:				                    (out.entro)
% Energy: 								    (out.energ)
% Homogeneity: 			                    (out.homom)
% Moment:									(out.momen)
% Correlation: matlab                       (out.corrm)
%
% The maximal correlation coefficient was not calculated due to
% computational instability 
% http://murphylab.web.cmu.edu/publications/boland/boland_node26.html
%


% 
% Normalize the glcm:
% Compute the sum of all the values in each glcm in the array and divide 
% each element by it sum
%
% Haralick uses 'Symmetric' = true in computing the glcm
% There is no Symmetric flag in the Matlab version I use hence
% I add the diagonally opposite pairs to obtain the Haralick glcm
% Here it is assumed that the diagonally opposite orientations are paired
% one after the other in the matrix
% If the above assumption is true with respect to the input glcm then
% setting the flag 'pairs' to 1 will compute the final glcms that would result 
% by setting 'Symmetric' to true. If your glcm is computed using the
% Matlab version with 'Symmetric' flag you can set the flag 'pairs' to 0
%
%
%


% If 'pairs' not entered: set pairs to 0 
if ((nargin > 2) || (nargin == 0))
   error('Too many or too few input arguments. Enter GLCM and pairs.');
elseif ( (nargin == 2) ) 
    if ((size(glcmin,1) <= 1) || (size(glcmin,2) <= 1))
       error('The GLCM should be a 2-D or 3-D matrix.');
    elseif ( size(glcmin,1) ~= size(glcmin,2) )
        error('Each GLCM should be square with NumLevels rows and NumLevels cols');
    end    
elseif (nargin == 1) % only GLCM is entered
    pairs = 0; % default is numbers and input 1 for percentage
    if ((size(glcmin,1) <= 1) || (size(glcmin,2) <= 1))
       error('The GLCM should be a 2-D or 3-D matrix.');
    elseif ( size(glcmin,1) ~= size(glcmin,2) )
       error('Each GLCM should be square with NumLevels rows and NumLevels cols');
    end    
end


format long e
if (pairs == 1)
    newn = 1;
    for nglcm = 1:2:size(glcmin,3)
        glcm(:,:,newn)  = glcmin(:,:,nglcm) + glcmin(:,:,nglcm+1);
        newn = newn + 1;
    end
elseif (pairs == 0)
    glcm = glcmin;
end

size_glcm_1 = size(glcm,1);
size_glcm_2 = size(glcm,2);
size_glcm_3 = size(glcm,3);

% checked
out.step = zeros(1,size_glcm_3);  % Step
out.contr = zeros(1,size_glcm_3); % Contrast
out.entro = zeros(1,size_glcm_3); % Entropy
out.energ = zeros(1,size_glcm_3); % Energy
out.homom = zeros(1,size_glcm_3); % Homogeneity
out.momen = zeros(1,size_glcm_3); % 3 Degree Moment
out.corrm = zeros(1,size_glcm_3); % Correlation

glcm_sum  = zeros(size_glcm_3,1);
glcm_mean = zeros(size_glcm_3,1);
glcm_var  = zeros(size_glcm_3,1);

% http://www.fp.ucalgary.ca/mhallbey/glcm_mean.htm confuses the range of 
% i and j used in calculating the means and standard deviations.
% As of now I am not sure if the range of i and j should be [1:Ng] or
% [0:Ng-1]. I am working on obtaining the values of mean and std that get
% the values of correlation that are provided by matlab.
u_x = zeros(size_glcm_3,1);
u_y = zeros(size_glcm_3,1);
s_x = zeros(size_glcm_3,1);
s_y = zeros(size_glcm_3,1);

% checked p_x p_y p_xplusy p_xminusy
p_x = zeros(size_glcm_1,size_glcm_3); % Ng x #glcms[1]  
p_y = zeros(size_glcm_2,size_glcm_3); % Ng x #glcms[1]
p_xplusy = zeros((size_glcm_1*2 - 1),size_glcm_3); %[1]
p_xminusy = zeros((size_glcm_1),size_glcm_3); %[1]
% checked hxy hxy1 hxy2 hx hy
hxy  = zeros(size_glcm_3,1);
hxy1 = zeros(size_glcm_3,1);
hx   = zeros(size_glcm_3,1);
hy   = zeros(size_glcm_3,1);
hxy2 = zeros(size_glcm_3,1);

corm = zeros(size_glcm_3,1);
corp = zeros(size_glcm_3,1);

for k = 1:size_glcm_3
    
    glcm_sum(k) = sum(sum(glcm(:,:,k)));
    glcm(:,:,k) = glcm(:,:,k)./glcm_sum(k); % Normalize each glcm
    glcm_mean(k) = mean2(glcm(:,:,k)); % compute mean after norm
    glcm_var(k)  = (std2(glcm(:,:,k)))^2;
    
    for i = 1:size_glcm_1
        
        for j = 1:size_glcm_2
            p_x(i,k) = p_x(i,k) + glcm(i,j,k); 
            p_y(i,k) = p_y(i,k) + glcm(j,i,k); % taking i for j and j for i
            %if (ismember((i + j),[2:2*size_glcm_1])) 
                p_xplusy((i+j)-1,k) = p_xplusy((i+j)-1,k) + glcm(i,j,k);
            %end
            %if (ismember(abs(i-j),[0:(size_glcm_1-1)])) 
                p_xminusy((abs(i-j))+1,k) = p_xminusy((abs(i-j))+1,k) +...
                    glcm(i,j,k);
            %end
        end
    end
    
end

% marginal probabilities are now available [1]
% p_xminusy has +1 in index for matlab (no 0 index)
% computing sum average, sum variance and sum entropy:


%Q    = zeros(size(glcm));

i_matrix  = repmat([1:size_glcm_1]',1,size_glcm_2);
j_matrix  = repmat([1:size_glcm_2],size_glcm_1,1);

i_index   = j_matrix(:);

j_index   = i_matrix(:);
xplusy_index = [1:(2*(size_glcm_1)-1)]';
xminusy_index = [0:(size_glcm_1-1)]';
mul_contr = abs(i_matrix - j_matrix).^2;
mul_momem = abs(i_matrix - j_matrix).^3;
mul_dissi = abs(i_matrix - j_matrix);


for k = 1:size_glcm_3 % number glcms
    
    out.step(k) = sum(sum(glcm(:,:,k)));
    out.contr(k) = sum(sum(mul_contr.*glcm(:,:,k)));
    out.entro(k) = - sum(sum((glcm(:,:,k).*log(glcm(:,:,k) + eps))));
    out.energ(k) = sum(sum(glcm(:,:,k).^2));
    out.homom(k) = sum(sum((glcm(:,:,k)./( 1 + mul_dissi))));
    out.momen(k) = sum(sum(mul_momem.*glcm(:,:,k)));
    
    u_x(k)       = sum(sum(i_matrix.*glcm(:,:,k))); 
    u_y(k)       = sum(sum(j_matrix.*glcm(:,:,k))); 
    % using http://www.fp.ucalgary.ca/mhallbey/glcm_variance.htm for s_x
    % s_y : This solves the difference in value of correlation and might be
    % the right value of standard deviations required 
    % According to this website there is a typo in [2] which provides
    % values of variance instead of the standard deviation hence a square
    % root is required as done below:
    s_x(k)  = (sum(sum( ((i_matrix - u_x(k)).^2).*glcm(:,:,k) )))^0.5;
    s_y(k)  = (sum(sum( ((j_matrix - u_y(k)).^2).*glcm(:,:,k) )))^0.5;
    
    corm(k) = sum(sum(((i_matrix - u_x(k)).*(j_matrix - u_y(k)).*glcm(:,:,k))));
    out.corrm(k) = corm(k) / (s_x(k)*s_y(k)); 
end
